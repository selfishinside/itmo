package ru.itmo.love.event;

import java.time.LocalDateTime;

/**
 * событие подтверждения бронирования после которого гостю асинхронно отправляется уведомление
 */
public record BookingConfirmedEvent(
        String eventId,
        Long bookingId,
        Long guestId,
        String guestEmail,
        String hotelName,
        LocalDateTime occurredAt
) {
}
