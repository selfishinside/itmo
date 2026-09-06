package ru.itmo.love.event;

import java.time.LocalDateTime;

/**
 * событие отправки рецензии которое запускает ее асинхронную модерацию
 */
public record ReviewSubmittedEvent(
        String eventId,
        Long reviewId,
        Long bookingId,
        Long hotelId,
        LocalDateTime occurredAt
) {
}
