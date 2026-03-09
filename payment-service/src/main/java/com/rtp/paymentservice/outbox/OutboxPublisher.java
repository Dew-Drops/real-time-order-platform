package com.rtp.paymentservice.outbox;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rtp.paymentservice.db.OutboxEventEntity;
import com.rtp.paymentservice.db.OutboxEventRepository;
import com.rtp.paymentservice.kafka.KafkaTopics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class OutboxPublisher {

    private static final Logger log = LoggerFactory.getLogger(OutboxPublisher.class);

    private final OutboxEventRepository outboxRepo;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public OutboxPublisher(OutboxEventRepository outboxRepo,
                           KafkaTemplate<String, String> kafkaTemplate,
                           ObjectMapper objectMapper) {
        this.outboxRepo = outboxRepo;
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    // every 2 seconds
    @Scheduled(fixedDelay = 2000)
    public void publishBatch() {
        List<OutboxEventEntity> batch = outboxRepo.findByStatusOrderByCreatedAtAsc("NEW", PageRequest.of(0, 20));
        if (batch.isEmpty()) return;

        for (OutboxEventEntity e : batch) {
            try {
                publishOne(e);
                markSent(e.getId());
            } catch (Exception ex) {
                log.error("Outbox publish failed. id={}, eventType={}", e.getId(), e.getEventType(), ex);
                markFailed(e.getId());
            }
        }
    }

    private void publishOne(OutboxEventEntity e) {
        String topic = e.getEventType();
        String key = e.getAggregateId();

        var msg = MessageBuilder.withPayload(e.getPayload())
                .setHeader(KafkaHeaders.TOPIC, topic)
                .setHeader(KafkaHeaders.KEY, key)
                .build();

        kafkaTemplate.send(msg);
        log.info("Outbox published. id={}, topic={}, key={}", e.getId(), topic, key);
    }

    @Transactional
    protected void markSent(Long id) {
        OutboxEventEntity e = outboxRepo.findById(id).orElse(null);
        if (e != null) e.markSent();
    }

    @Transactional
    protected void markFailed(Long id) {
        OutboxEventEntity e = outboxRepo.findById(id).orElse(null);
        if (e != null) e.markFailed();
    }
}