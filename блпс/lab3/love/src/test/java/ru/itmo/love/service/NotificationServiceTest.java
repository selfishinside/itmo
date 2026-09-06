package ru.itmo.love.service;

import org.junit.jupiter.api.Test;
import ru.itmo.love.entity.Notification;
import ru.itmo.love.entity.enums.NotificationStatus;
import ru.itmo.love.event.BookingConfirmedEvent;
import ru.itmo.love.integration.bitrix.BitrixResult;
import ru.itmo.love.repository.NotificationRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Проверяет идемпотентное создание и отправку уведомления.
 */
class NotificationServiceTest {

    /** Проверяет, что новое Kafka-событие создаёт одно SENT-уведомление. */
    @Test
    void bookingConfirmedCreatesAndSendsNotification() {
        NotificationRepository repository = mock(NotificationRepository.class);
        BitrixConnector bitrixConnector = mock(BitrixConnector.class);
        NotificationService service = new NotificationService(repository, bitrixConnector);
        when(repository.findByIdempotencyKey(any())).thenReturn(Optional.empty());
        when(repository.save(any())).thenAnswer(invocation -> {
            Notification notification = invocation.getArgument(0);
            notification.setId(1L);
            return notification;
        });
        when(bitrixConnector.send(any())).thenReturn(new BitrixResult(true, "B-1", "OK"));

        Notification result = service.handleBookingConfirmed(new BookingConfirmedEvent(
                "event-1", 10L, 20L, "guest@example.com", "Hotel", LocalDateTime.now()));

        assertEquals(NotificationStatus.SENT, result.getStatus());
        assertEquals("B-1", result.getBitrixTaskId());
        verify(bitrixConnector).send(any());
    }
}
