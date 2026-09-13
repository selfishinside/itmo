package ru.itmo.love.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import ru.itmo.love.event.BookingConfirmedEvent;
import ru.itmo.love.event.CheckInReminderEvent;
import ru.itmo.love.event.ReviewSubmittedEvent;

/**
 * шлет события в kafka
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EventPublisher implements EventPublisherInt {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${app.kafka.topics.booking-confirmed}")
    private String bookingConfirmedTopic;

    @Value("${app.kafka.topics.review-submitted}")
    private String reviewSubmittedTopic;

    @Value("${app.kafka.topics.check-in-reminder}")
    private String checkInReminderTopic;

    /** шлет подтверждение брони */
    @Override
    public void publishBookingConfirmed(BookingConfirmedEvent event) {
        publish(bookingConfirmedTopic, event.bookingId().toString(), event.eventId(), event);
    }

    /** шлет рецензию */
    @Override
    public void publishReviewSubmitted(ReviewSubmittedEvent event) {
        publish(reviewSubmittedTopic, event.hotelId().toString(), event.eventId(), event);
    }

    /** шлет напоминание */
    @Override
    public void publishCheckInReminder(CheckInReminderEvent event) {
        publish(checkInReminderTopic, event.bookingId().toString(), event.eventId(), event);
    }

    private void publish(String topic, String key, String eventId, Object event) {
        if (TransactionSynchronizationManager.isActualTransactionActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                /** шлет после commit */
                @Override
                public void afterCommit() {
                    send(topic, key, eventId, event);
                }
            });
            return;
        }
        send(topic, key, eventId, event);
    }

    private void send(String topic, String key, String eventId, Object event) {
        kafkaTemplate.send(topic, key, event).whenComplete((result, error) -> {
            if (error != null) {
                log.error("Kafka publish failed: topic={}, eventId={}", topic, eventId, error);
            } else {
                log.info("Kafka event published: topic={}, partition={}, eventId={}",
                        topic, result.getRecordMetadata().partition(), eventId);
            }
        });
    }
}
