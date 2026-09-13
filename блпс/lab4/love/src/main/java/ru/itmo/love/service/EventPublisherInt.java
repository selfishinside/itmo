package ru.itmo.love.service;

import ru.itmo.love.event.BookingConfirmedEvent;
import ru.itmo.love.event.CheckInReminderEvent;
import ru.itmo.love.event.ReviewSubmittedEvent;

/**
 * контракт kafka producer
 */
public interface EventPublisherInt {

    /** шлет подтверждение брони */
    void publishBookingConfirmed(BookingConfirmedEvent event);

    /** шлет рецензию */
    void publishReviewSubmitted(ReviewSubmittedEvent event);

    /** шлет напоминание */
    void publishCheckInReminder(CheckInReminderEvent event);
}
