package ru.itmo.love.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

/**
 * топики kafka
 */
@Configuration
public class KafkaTopicsConfig {

    /** топик подтверждений */
    @Bean
    public NewTopic bookingConfirmedTopic(@Value("${app.kafka.topics.booking-confirmed}") String topic) {
        return TopicBuilder.name(topic).partitions(2).replicas(1).build();
    }

    /** топик рецензий */
    @Bean
    public NewTopic reviewSubmittedTopic(@Value("${app.kafka.topics.review-submitted}") String topic) {
        return TopicBuilder.name(topic).partitions(2).replicas(1).build();
    }

    /** топик напоминаний */
    @Bean
    public NewTopic checkInReminderTopic(@Value("${app.kafka.topics.check-in-reminder}") String topic) {
        return TopicBuilder.name(topic).partitions(2).replicas(1).build();
    }

    /** топик сломанных событий */
    @Bean
    public NewTopic deadLetterTopic() {
        return TopicBuilder.name("love.events.DLT").partitions(2).replicas(1).build();
    }
}
