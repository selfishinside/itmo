package ru.itmo.love.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.itmo.love.event.BookingConfirmedEvent;
import ru.itmo.love.event.CheckInReminderEvent;
import ru.itmo.love.service.NotificationServiceInt;

/**
 * consumer уведомлений
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationConsumer {

    private static final String GROUP_ID = "notification-consuming-group";
    private final NotificationServiceInt notificationService;

    @Value("${app.node-id}")
    private String nodeId;

    /** ловит подтверждение брони */
    @KafkaListener(topics = "${app.kafka.topics.booking-confirmed}", groupId = GROUP_ID)
    public void consumeBookingConfirmed(BookingConfirmedEvent event) {
        log.info("Received event: nodeId={}, groupId={}, eventId={}, bookingId={}",
                nodeId, GROUP_ID, event.eventId(), event.bookingId());
        notificationService.handleBookingConfirmed(event);
    }

    /** ловит напоминание */
    @KafkaListener(topics = "${app.kafka.topics.check-in-reminder}", groupId = GROUP_ID)
    public void consumeCheckInReminder(CheckInReminderEvent event) {
        log.info("Received event: nodeId={}, groupId={}, eventId={}, bookingId={}",
                nodeId, GROUP_ID, event.eventId(), event.bookingId());
        notificationService.handleCheckInReminder(event);
    }
}
