package ru.itmo.love.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.itmo.love.event.CheckInReminderEvent;
import ru.itmo.love.service.BookingServiceInt;
import ru.itmo.love.service.EventPublisherInt;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * ежедневно публикует напоминания гостям у которых заселение запланировано на завтра
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class CheckInReminderScheduler {

    private final BookingServiceInt bookingService;
    private final EventPublisherInt eventPublisher;

    /** запускает поиск броней и возвращает число опубликованных напоминаний */
    @Scheduled(cron = "${app.scheduling.check-in-reminder-cron}")
    @Transactional(readOnly = true)
    public int publishTomorrowReminders() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        var bookings = bookingService.getConfirmedBookingsForCheckIn(tomorrow);
        bookings.forEach(booking -> eventPublisher.publishCheckInReminder(new CheckInReminderEvent(
                UUID.randomUUID().toString(), booking.getId(), booking.getGuest().getId(),
                booking.getCheckInDate(), LocalDateTime.now())));
        log.info("Check-in reminder scheduler published {} events", bookings.size());
        return bookings.size();
    }
}
