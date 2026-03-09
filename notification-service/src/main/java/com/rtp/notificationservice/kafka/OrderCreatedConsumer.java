package com.rtp.notificationservice.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Component
public class OrderCreatedConsumer {

    private static final Logger log = LoggerFactory.getLogger(OrderCreatedConsumer.class);
    public static final String CORRELATION_ID = "X-Correlation-Id";

    @KafkaListener(topics = "order.created", groupId = "notification-service-group")
    public void onOrderCreated(
            OrderCreatedEvent event,
            @Header(value = CORRELATION_ID, required = false) String correlationId,
            @Header(value = KafkaHeaders.RECEIVED_KEY, required = false) String key,
    Acknowledgment ack) {
        if (correlationId != null) {
            MDC.put(CORRELATION_ID, correlationId);
        }

        try {
            log.info("Notification Service received OrderCreatedEvent: key={}, event={}",
                    key, toSafeString(event));
            // Simulate failure (test)
            if (event.getQuantity() == 13) {
                throw new RuntimeException("Simulated processing failure");
            }

            // later: call Email/SMS provider here
            log.info("Email notification simulated for orderId={}", event.getOrderId());
            ack.acknowledge(); //commits offset
        } catch (Exception e) {
            log.error("Failed processing message key={}, event={}", key, toSafeString(event), e);
            throw e; // important so error handler can retry / DLT
        } finally {
            MDC.remove(CORRELATION_ID);
        }
    }

    private String toSafeString(OrderCreatedEvent e) {
        if (e == null) return "null";
        return "OrderCreatedEvent{orderId=" + e.getOrderId()
                + ", productName='" + e.getProductName()
                + "', quantity=" + e.getQuantity() + "}";
    }
}
