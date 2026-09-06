package ru.itmo.love.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.itmo.love.event.ReviewSubmittedEvent;
import ru.itmo.love.service.ReviewServiceInt;

/**
 * consumer рецензий
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ReviewModerationConsumer {

    private static final String GROUP_ID = "review-processing-group";
    private final ReviewServiceInt reviewService;

    @Value("${app.node-id}")
    private String nodeId;

    /** модерирует рецензию */
    @KafkaListener(topics = "${app.kafka.topics.review-submitted}", groupId = GROUP_ID)
    public void consume(ReviewSubmittedEvent event) {
        log.info("Received event: nodeId={}, groupId={}, eventId={}, reviewId={}, bookingId={}",
                nodeId, GROUP_ID, event.eventId(), event.reviewId(), event.bookingId());
        reviewService.moderate(event);
    }
}
