package ru.itmo.love.event;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * событие напоминания о завтрашнем заселении создаваемое планировщиком
 */
public record CheckInReminderEvent(
        String eventId,
        Long bookingId,
        Long guestId,
        LocalDate checkInDate,
        LocalDateTime occurredAt
) {
}
