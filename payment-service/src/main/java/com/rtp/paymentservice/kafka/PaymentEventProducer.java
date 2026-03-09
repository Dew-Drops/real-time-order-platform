package com.rtp.paymentservice.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
public class PaymentEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public PaymentEventProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishPaymentSucceeded(OrderCreatedEvent orderEvent, String paymentId) {
        PaymentSucceededEvent event = new PaymentSucceededEvent();
        event.setEventId(UUID.randomUUID().toString());
        event.setOrderId(orderEvent.getOrderId());
        event.setPaymentId(paymentId);

        event.setCorrelationId(orderEvent.getCorrelationId());
        event.setTimestamp(Instant.now());
        event.setSource("payment-service");
        event.setVersion(1);

        var message = MessageBuilder.withPayload(event)
                .setHeader(KafkaHeaders.TOPIC, KafkaTopics.PAYMENT_SUCCEEDED)
                .setHeader(KafkaHeaders.KEY, String.valueOf(event.getOrderId()))
                .build();

        kafkaTemplate.send(message);
    }

    public void publishPaymentFailed(OrderCreatedEvent orderEvent, String reason) {
        PaymentFailedEvent event = new PaymentFailedEvent();
        event.setEventId(UUID.randomUUID().toString());
        event.setOrderId(orderEvent.getOrderId());
        event.setReason(reason);

        event.setCorrelationId(orderEvent.getCorrelationId());
        event.setTimestamp(Instant.now());
        event.setSource("payment-service");
        event.setVersion(1);

        var message = MessageBuilder.withPayload(event)
                .setHeader(KafkaHeaders.TOPIC, KafkaTopics.PAYMENT_FAILED)
                .setHeader(KafkaHeaders.KEY, String.valueOf(event.getOrderId()))
                .build();

        kafkaTemplate.send(message);
    }
}
