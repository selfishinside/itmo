package ru.itmo.love.service;

import ru.itmo.love.entity.Notification;
import ru.itmo.love.event.BookingConfirmedEvent;
import ru.itmo.love.event.CheckInReminderEvent;

import java.util.List;

/**
 * контракт уведомлений
 */
public interface NotificationServiceInt {

    /** событие брони */
    Notification handleBookingConfirmed(BookingConfirmedEvent event);

    /** событие напоминания */
    Notification handleCheckInReminder(CheckInReminderEvent event);

    /** повтор failed */
    int retryFailed();

    /** все уведомления */
    List<Notification> getAll();
}
