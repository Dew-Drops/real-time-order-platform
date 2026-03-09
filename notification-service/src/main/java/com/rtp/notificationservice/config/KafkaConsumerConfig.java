package com.rtp.notificationservice.config;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
public class KafkaConsumerConfig {

    @Bean
    public DefaultErrorHandler errorHandler(KafkaTemplate<Object, Object> kafkaTemplate) {

        // Send failed message to <topic>.DLT
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(
                kafkaTemplate,
                (ConsumerRecord<?, ?> record, Exception ex) -> new TopicPartition(record.topic() + "notification.DLT", record.partition())
        );

        // Retry: 3 attempts total = 1 initial + 2 retries (depends how you count)
        // Here: 2 retries after failure, 1 second gap
        FixedBackOff backOff = new FixedBackOff(0L, 0L);

        DefaultErrorHandler handler = new DefaultErrorHandler(recoverer, backOff);

        // Optional: don't retry these (examples)
        // handler.addNotRetryableExceptions(IllegalArgumentException.class);

        return handler;
    }
}
