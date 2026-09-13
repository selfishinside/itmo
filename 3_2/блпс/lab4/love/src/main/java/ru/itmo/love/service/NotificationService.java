package ru.itmo.love.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.itmo.love.entity.Notification;
import ru.itmo.love.entity.enums.NotificationStatus;
import ru.itmo.love.entity.enums.NotificationType;
import ru.itmo.love.event.BookingConfirmedEvent;
import ru.itmo.love.event.CheckInReminderEvent;
import ru.itmo.love.integration.bitrix.BitrixPayload;
import ru.itmo.love.repository.NotificationRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * сервис уведомлений
 */
@Service
@RequiredArgsConstructor
public class NotificationService implements NotificationServiceInt {

    private final NotificationRepository notificationRepository;
    private final BitrixConnectorInt bitrixConnector;

    /** создает уведомление по брони */
    @Override
    @Transactional
    public Notification handleBookingConfirmed(BookingConfirmedEvent event) {
        String key = "booking-confirmed:" + event.bookingId() + ":" + event.eventId();
        return notificationRepository.findByIdempotencyKey(key)
                .orElseGet(() -> createAndSend(event.bookingId(), event.guestId(), key,
                        "Booking " + event.bookingId() + " at " + event.hotelName() + " is confirmed"));
    }

    /** создает напоминание */
    @Override
    @Transactional
    public Notification handleCheckInReminder(CheckInReminderEvent event) {
        String key = "check-in-reminder:" + event.bookingId() + ":" + event.checkInDate();
        return notificationRepository.findByIdempotencyKey(key)
                .orElseGet(() -> createAndSend(event.bookingId(), event.guestId(), key,
                        "Reminder: check-in for booking " + event.bookingId() + " is tomorrow"));
    }

    /** повторяет failed */
    @Override
    @Transactional
    public int retryFailed() {
        List<Notification> failed = notificationRepository.findByStatus(NotificationStatus.FAILED);
        failed.forEach(this::send);
        return failed.size();
    }

    /** отдает все уведомления */
    @Override
    @Transactional(readOnly = true)
    public List<Notification> getAll() {
        return notificationRepository.findAll();
    }

    private Notification createAndSend(Long bookingId, Long guestId, String key, String message) {
        Notification notification = notificationRepository.save(Notification.builder()
                .bookingId(bookingId)
                .guestId(guestId)
                .type(NotificationType.EMAIL)
                .status(NotificationStatus.PENDING)
                .message(message)
                .retryCount(0)
                .createdAt(LocalDateTime.now())
                .idempotencyKey(key)
                .build());
        return send(notification);
    }

    private Notification send(Notification notification) {
        try {
            var result = bitrixConnector.send(new BitrixPayload("NOTIFICATION", notification.getId(),
                    "SEND_NOTIFICATION", Map.of("message", notification.getMessage())));
            if (!result.success()) {
                throw new IllegalStateException(result.response());
            }
            notification.setStatus(NotificationStatus.SENT);
            notification.setSentAt(LocalDateTime.now());
            notification.setBitrixTaskId(result.externalId());
        } catch (RuntimeException exception) {
            notification.setStatus(NotificationStatus.FAILED);
            notification.setRetryCount(notification.getRetryCount() + 1);
        }
        return notificationRepository.save(notification);
    }
}
