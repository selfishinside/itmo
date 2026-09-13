package ru.itmo.love.bpm;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.client.ExternalTaskClient;
import org.camunda.bpm.client.task.ExternalTask;
import org.camunda.bpm.client.task.ExternalTaskService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import ru.itmo.love.dto.booking.CreateBookingRequest;
import ru.itmo.love.dto.guest.CreateGuestRequest;
import ru.itmo.love.dto.hotel.HotelDTO;
import ru.itmo.love.dto.review.SubmitReviewRequest;
import ru.itmo.love.entity.enums.ReviewStatus;
import ru.itmo.love.event.BookingConfirmedEvent;
import ru.itmo.love.event.CheckInReminderEvent;
import ru.itmo.love.event.ReviewSubmittedEvent;
import ru.itmo.love.scheduler.CheckInReminderScheduler;
import ru.itmo.love.scheduler.NotificationRetryScheduler;
import ru.itmo.love.scheduler.RatingUpdateScheduler;
import ru.itmo.love.service.*;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDate;
import java.util.*;

/** выполняет external tasks из standalone camunda */
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "app.camunda", name = "worker-enabled", havingValue = "true", matchIfMissing = true)
public class ExternalTaskWorker {
    private final BookingActivitiesInt bookings;
    private final GuestServiceInt guests;
    private final HotelServiceInt hotels;
    private final ReviewServiceInt reviews;
    private final NotificationServiceInt notifications;
    private final CheckInReminderScheduler reminders;
    private final NotificationRetryScheduler retry;
    private final RatingUpdateScheduler ratings;
    private final ObjectMapper mapper;

    @Value("${app.camunda.base-url}")
    private String baseUrl;

    @Value("${app.camunda.worker-id}")
    private String workerId;

    @Value("${app.camunda.lock-duration}")
    private long lockDuration;

    @Value("${app.camunda.async-response-timeout}")
    private long asyncResponseTimeout;

    private ExternalTaskClient client;

    @PostConstruct
    public void start() {
        client = ExternalTaskClient.create()
                .baseUrl(baseUrl)
                .workerId(workerId)
                .asyncResponseTimeout(asyncResponseTimeout)
                .build();
        for (String topic : topics()) {
            client.subscribe(topic).lockDuration(lockDuration).handler(this::handle).open();
        }
    }

    @PreDestroy
    public void stop() {
        if (client != null) client.stop();
    }

    private void handle(ExternalTask task, ExternalTaskService service) {
        try {
            Map<String, Object> variables = switch (task.getTopicName()) {
                case "createBooking" -> createBooking(task);
                case "recordPayment" -> run(() -> bookings.confirmPayment(number(task, "bookingId")));
                case "confirmBooking" -> run(() -> bookings.confirmBookingByHotel(number(task, "bookingId")));
                case "requestCancellation" -> run(() -> bookings.cancelBooking(
                        number(task, "bookingId"), string(task, "reason")));
                case "approveCancellation" -> run(() -> bookings.cancelBookingByHotel(
                        number(task, "bookingId"), true, string(task, "reason")));
                case "rejectCancellation" -> run(() -> bookings.cancelBookingByHotel(
                        number(task, "bookingId"), false, string(task, "reason")));
                case "expirePayment" -> run(() -> bookings.expirePayment(number(task, "bookingId")));
                case "completeStay" -> run(() -> bookings.completeStay(number(task, "bookingId")));
                case "saveGuest" -> saveGuest(task);
                case "saveHotel" -> saveHotel(task);
                case "saveReview" -> saveReview(task);
                case "sendReminders" -> Map.of("count", reminders.publishTomorrowReminders());
                case "retryNotifications" -> Map.of("count", retry.retryFailedNotifications());
                case "updateRatings" -> Map.of("count", ratings.recalculateRatings());
                case "sweepPayments" -> Map.of("count", 0);
                case "notifyBooking" -> notifyBooking(task);
                case "notifyReminder" -> notifyReminder(task);
                case "inspectReview" -> inspectReview(task);
                case "rejectReview" -> run(() -> reviews.reject(number(task, "reviewId")));
                case "publishReview" -> run(() -> reviews.publish(number(task, "reviewId")));
                case "refreshRating" -> run(() -> reviews.recalculateHotelRating(number(task, "hotelId")));
                default -> throw new IllegalArgumentException("Unknown external task topic " + task.getTopicName());
            };
            service.complete(task, variables);
        } catch (Exception e) {
            service.handleFailure(task, e.getMessage(), stackTrace(e), 0, 0);
        }
    }

    private Map<String, Object> createBooking(ExternalTask task) {
        LocalDate in = LocalDate.parse(string(task, "checkInDate"));
        LocalDate out = LocalDate.parse(string(task, "checkOutDate"));
        if (!out.isAfter(in)) throw new IllegalArgumentException("Check-out must follow check-in");
        var request = CreateBookingRequest.builder()
                .roomId(number(task, "roomId"))
                .guestId(number(task, "guestId"))
                .checkInDate(in)
                .checkOutDate(out)
                .numberOfGuests(((Number) task.getVariable("numberOfGuests")).intValue())
                .cardNumber(string(task, "cardNumber"))
                .cardHolder(string(task, "cardHolder"))
                .build();
        var booking = bookings.createBooking(request);
        Date checkoutAt = Date.from(out.atStartOfDay(java.time.ZoneId.systemDefault()).toInstant());
        return Map.of("bookingId", booking.getId(), "checkoutAt", checkoutAt);
    }

    private Map<String, Object> saveGuest(ExternalTask task) {
        var request = mapper.convertValue(task.getAllVariables(), CreateGuestRequest.class);
        return Map.of("resultId", guests.createGuest(request).getId());
    }

    private Map<String, Object> saveHotel(ExternalTask task) throws Exception {
        Map<String, Object> input = new HashMap<>(task.getAllVariables());
        Object rooms = input.remove("roomsJson");
        var dto = mapper.convertValue(input, HotelDTO.class);
        if (rooms != null && !rooms.toString().isBlank()) {
            dto.setRooms(mapper.readValue(rooms.toString(),
                    mapper.getTypeFactory().constructCollectionType(List.class, ru.itmo.love.dto.hotel.RoomDTO.class)));
        }
        return Map.of("resultId", hotels.createHotel(dto).getId());
    }

    private Map<String, Object> saveReview(ExternalTask task) {
        var request = mapper.convertValue(task.getAllVariables(), SubmitReviewRequest.class);
        return Map.of("resultId", reviews.submit(request).getId());
    }

    private Map<String, Object> notifyBooking(ExternalTask task) throws Exception {
        notifications.handleBookingConfirmed(mapper.readValue(string(task, "eventJson"), BookingConfirmedEvent.class));
        return Map.of();
    }

    private Map<String, Object> notifyReminder(ExternalTask task) throws Exception {
        notifications.handleCheckInReminder(mapper.readValue(string(task, "eventJson"), CheckInReminderEvent.class));
        return Map.of();
    }

    private Map<String, Object> inspectReview(ExternalTask task) throws Exception {
        var event = mapper.readValue(string(task, "eventJson"), ReviewSubmittedEvent.class);
        var review = reviews.getById(event.reviewId());
        return Map.of(
                "reviewId", review.getId(),
                "hotelId", review.getHotelId(),
                "reviewComment", Objects.toString(review.getComment(), ""),
                "pending", review.getStatus() == ReviewStatus.PENDING_MODERATION
        );
    }

    private Map<String, Object> run(Runnable action) {
        action.run();
        return Map.of();
    }

    private Long number(ExternalTask task, String name) {
        return ((Number) task.getVariable(name)).longValue();
    }

    private String string(ExternalTask task, String name) {
        Object value = task.getVariable(name);
        return value == null ? null : value.toString();
    }

    private String stackTrace(Exception e) {
        StringWriter out = new StringWriter();
        e.printStackTrace(new PrintWriter(out));
        return out.toString();
    }

    private List<String> topics() {
        return List.of(
                "createBooking", "recordPayment", "confirmBooking", "requestCancellation",
                "approveCancellation", "rejectCancellation", "expirePayment", "completeStay",
                "saveGuest", "saveHotel", "saveReview", "sendReminders", "retryNotifications",
                "updateRatings", "sweepPayments", "notifyBooking", "notifyReminder", "inspectReview",
                "rejectReview", "publishReview", "refreshRating"
        );
    }
}
