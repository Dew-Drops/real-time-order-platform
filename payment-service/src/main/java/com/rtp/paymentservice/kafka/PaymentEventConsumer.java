package com.rtp.paymentservice.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rtp.paymentservice.db.OutboxEventEntity;
import com.rtp.paymentservice.db.OutboxEventRepository;
import com.rtp.paymentservice.db.PaymentEntity;
import com.rtp.paymentservice.db.PaymentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Random;
import java.util.UUID;

@Component
public class PaymentEventConsumer {
    private static final Logger log = LoggerFactory.getLogger(PaymentEventConsumer.class);
    private static final String CORRELATION_ID = "X-Correlation-Id";

    private final Random random = new Random();
    private final PaymentRepository paymentRepository;
    private final OutboxEventRepository outboxRepo;
    private final ObjectMapper objectMapper;
    public PaymentEventConsumer(PaymentRepository paymentRepository,
                                OutboxEventRepository outboxRepo,
                                ObjectMapper objectMapper) {
        this.paymentRepository = paymentRepository;
        this.outboxRepo = outboxRepo;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = KafkaTopics.ORDER_CREATED, groupId = "payment-service-group")
    public void onOrderCreated(
            OrderCreatedEvent event,
            @Header(value = CORRELATION_ID, required = false) String correlationId,
            @Header(value = KafkaHeaders.RECEIVED_KEY, required = false) String key,
            Acknowledgment ack
    ) {
        if (correlationId != null && !correlationId.isBlank()) MDC.put(CORRELATION_ID, correlationId);
        try {
            log.info("Payment Service received order.created. key={}, orderId={}", key, event.getOrderId());

            //If already decided for this order, just ACK and return (no republish here)
            if (paymentRepository.findByOrderId(event.getOrderId()).isPresent()) {
                log.info("Duplicate order.created for orderId={} -> already processed. ACK.", event.getOrderId());
                ack.acknowledge();
                return;
            }
            processAndWriteOutbox(event);
            ack.acknowledge();
        } catch (Exception ex) {
            log.error("Error processing payment for orderId={}", event.getOrderId(), ex);
            throw ex; // retry / DLT
        } finally {
            MDC.clear();
        }
    }

    @Transactional
    protected void processAndWriteOutbox(OrderCreatedEvent orderEvent) {
        boolean success = random.nextInt(100) < 70;
        try {
            if (success)
            {
                String paymentId = UUID.randomUUID().toString();
                paymentRepository.save(new PaymentEntity(
                        orderEvent.getOrderId(),
                        "SUCCEEDED",
                        paymentId,
                        null,
                        orderEvent.getCorrelationId()
                ));
                PaymentSucceededEvent ev = new PaymentSucceededEvent();
                ev.setEventId(UUID.randomUUID().toString());
                ev.setOrderId(orderEvent.getOrderId());
                ev.setPaymentId(paymentId);
                ev.setCorrelationId(orderEvent.getCorrelationId());
                ev.setTimestamp(Instant.now());
                ev.setSource("payment-service");
                ev.setVersion(1);
                String payload = objectMapper.writeValueAsString(ev);
                outboxRepo.save(new OutboxEventEntity(
                        String.valueOf(orderEvent.getOrderId()),
                        KafkaTopics.PAYMENT_SUCCEEDED,
                        payload,
                        orderEvent.getCorrelationId()
                ));
            } else {
                String reason = "Insufficient balance";
                paymentRepository.save(new PaymentEntity(
                        orderEvent.getOrderId(),
                        "FAILED",
                        null,
                        reason,
                        orderEvent.getCorrelationId()
                ));
                PaymentFailedEvent ev = new PaymentFailedEvent();
                ev.setEventId(UUID.randomUUID().toString());
                ev.setOrderId(orderEvent.getOrderId());
                ev.setReason(reason);
                ev.setCorrelationId(orderEvent.getCorrelationId());
                ev.setTimestamp(Instant.now());
                ev.setSource("payment-service");
                ev.setVersion(1);
                String payload = objectMapper.writeValueAsString(ev);
                outboxRepo.save(new OutboxEventEntity(
                        String.valueOf(orderEvent.getOrderId()),
                        KafkaTopics.PAYMENT_FAILED,
                        payload,
                        orderEvent.getCorrelationId()
                ));
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize event payload", e);
        }
    }
}