package ru.itmo.love.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.itmo.love.dto.common.ApiResponse;
import ru.itmo.love.dto.booking.BookingDTO;
import ru.itmo.love.entity.Booking;
import ru.itmo.love.entity.IntegrationLog;
import ru.itmo.love.entity.Notification;
import ru.itmo.love.entity.Review;
import ru.itmo.love.event.BookingConfirmedEvent;
import ru.itmo.love.scheduler.CheckInReminderScheduler;
import ru.itmo.love.scheduler.NotificationRetryScheduler;
import ru.itmo.love.scheduler.RatingUpdateScheduler;
import ru.itmo.love.service.BitrixConnectorInt;
import ru.itmo.love.service.BookingServiceInt;
import ru.itmo.love.service.EventPublisherInt;
import ru.itmo.love.service.NotificationServiceInt;
import ru.itmo.love.service.ReviewServiceInt;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * ручки для показа лр3
 */
@RestController
@RequestMapping("/api/v1/lab3")
@RequiredArgsConstructor
public class Lab3Controller {

    private final NotificationServiceInt notificationService;
    private final BitrixConnectorInt bitrixConnector;
    private final CheckInReminderScheduler checkInReminderScheduler;
    private final NotificationRetryScheduler notificationRetryScheduler;
    private final RatingUpdateScheduler ratingUpdateScheduler;
    private final BookingServiceInt bookingService;
    private final EventPublisherInt eventPublisher;
    private final ReviewServiceInt reviewService;

    @Value("${app.node-id}")
    private String nodeId;

    /** показывает узел */
    @GetMapping("/node")
    @PreAuthorize("hasAuthority(T(ru.itmo.love.security.policy.Privilege).LAB3_MONITOR)")
    public ResponseEntity<ApiResponse<Map<String, String>>> node() {
        return ResponseEntity.ok(ApiResponse.success(Map.of("nodeId", nodeId), "Current application node"));
    }

    /** показывает уведомления */
    @GetMapping("/notifications")
    @PreAuthorize("hasAuthority(T(ru.itmo.love.security.policy.Privilege).LAB3_MONITOR)")
    public ResponseEntity<ApiResponse<List<Notification>>> notifications() {
        return ResponseEntity.ok(ApiResponse.success(notificationService.getAll(), "Notifications found"));
    }

    /** показывает логи bitrix */
    @GetMapping("/integration-logs")
    @PreAuthorize("hasAuthority(T(ru.itmo.love.security.policy.Privilege).LAB3_MONITOR)")
    public ResponseEntity<ApiResponse<List<IntegrationLog>>> integrationLogs() {
        return ResponseEntity.ok(ApiResponse.success(bitrixConnector.getLogs(), "Integration logs found"));
    }

    /**
     * кидает событие брони
     * можно дергать много раз
     */
    @PostMapping("/demo/kafka-booking-confirmed/{bookingId}")
    @PreAuthorize("hasAuthority(T(ru.itmo.love.security.policy.Privilege).SCHEDULER_RUN)")
    public ResponseEntity<ApiResponse<Map<String, Object>>> publishDemoBookingConfirmed(
            @PathVariable Long bookingId) {
        return publishBookingConfirmedEvent(bookingId);
    }

    /**
     * сам берет бронь
     * удобно для helios
     */
    @PostMapping("/demo/kafka-booking-confirmed")
    @PreAuthorize("hasAuthority(T(ru.itmo.love.security.policy.Privilege).SCHEDULER_RUN)")
    public ResponseEntity<ApiResponse<Map<String, Object>>> publishDemoBookingConfirmed() {
        Long bookingId = bookingService.getAllBookings().stream()
                .findFirst()
                .map(BookingDTO::getId)
                .orElse(null);
        if (bookingId == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("No bookings found for Kafka demo", "BOOKING_NOT_FOUND"));
        }
        return publishBookingConfirmedEvent(bookingId);
    }

    /** шлет событие брони */
    private ResponseEntity<ApiResponse<Map<String, Object>>> publishBookingConfirmedEvent(Long bookingId) {
        Booking booking;
        try {
            booking = bookingService.getBookingEntityById(bookingId);
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(exception.getMessage(), "BOOKING_NOT_FOUND"));
        }
        String eventId = UUID.randomUUID().toString();

        eventPublisher.publishBookingConfirmed(new BookingConfirmedEvent(
                eventId,
                booking.getId(),
                booking.getGuest().getId(),
                booking.getGuest().getEmail(),
                booking.getRoom().getHotel().getName(),
                LocalDateTime.now()
        ));

        return ResponseEntity.accepted().body(ApiResponse.success(
                Map.of("eventId", eventId, "bookingId", bookingId),
                "Unique demo event published to Kafka"
        ));
    }

    /**
     * кидает рецензию еще раз
     * нужно для повторного теста
     */
    @PostMapping("/demo/kafka-review-submitted/{reviewId}")
    @PreAuthorize("hasAuthority(T(ru.itmo.love.security.policy.Privilege).SCHEDULER_RUN)")
    public ResponseEntity<ApiResponse<Review>> publishDemoReviewSubmitted(@PathVariable Long reviewId) {
        return ResponseEntity.accepted().body(ApiResponse.success(
                reviewService.requeueForDemo(reviewId),
                "Review requeued for Kafka moderation"
        ));
    }

    /** запускает reminder scheduler */
    @PostMapping("/schedulers/check-in-reminders")
    @PreAuthorize("hasAuthority(T(ru.itmo.love.security.policy.Privilege).SCHEDULER_RUN)")
    public ResponseEntity<ApiResponse<Integer>> checkInReminders() {
        int count = checkInReminderScheduler.publishTomorrowReminders();
        return ResponseEntity.ok(ApiResponse.success(count, "Check-in reminders published"));
    }

    /** запускает retry scheduler */
    @PostMapping("/schedulers/notification-retry")
    @PreAuthorize("hasAuthority(T(ru.itmo.love.security.policy.Privilege).SCHEDULER_RUN)")
    public ResponseEntity<ApiResponse<Integer>> notificationRetry() {
        int count = notificationRetryScheduler.retryFailedNotifications();
        return ResponseEntity.ok(ApiResponse.success(count, "Failed notifications retried"));
    }

    /** запускает rating scheduler */
    @PostMapping("/schedulers/rating-update")
    @PreAuthorize("hasAuthority(T(ru.itmo.love.security.policy.Privilege).SCHEDULER_RUN)")
    public ResponseEntity<ApiResponse<Integer>> ratingUpdate() {
        int count = ratingUpdateScheduler.recalculateRatings();
        return ResponseEntity.ok(ApiResponse.success(count, "Hotel ratings recalculated"));
    }
}
