package com.rtp.orderservice.kafka;

import com.rtp.orderservice.event.PaymentFailedEvent;
import com.rtp.orderservice.event.PaymentSucceededEvent;
import com.rtp.orderservice.service.OrderSagaService;
import org.slf4j.MDC;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
@Component
public class PaymentEventConsumer {
    private static final String CORRELATION_ID = "X-Correlation-Id";
    private final OrderSagaService sagaService;

    public PaymentEventConsumer(OrderSagaService sagaService) {
        this.sagaService = sagaService;
    }
    @KafkaListener(topics = KafkaTopics.PAYMENT_SUCCEEDED, groupId = "order-service")
    public void onPaymentSucceeded(PaymentSucceededEvent event, Acknowledgment ack) {
        try {
            if (event.getCorrelationId() != null) MDC.put(CORRELATION_ID, event.getCorrelationId());
            sagaService.handlePaymentSucceeded(event.getEventId(), event.getOrderId(), KafkaTopics.PAYMENT_SUCCEEDED);
            ack.acknowledge(); //only after success/ack after saga processing (duplicate-safe)
        } catch (Exception ex) {
            //do not ack; allow retry/DLT
            throw ex;
        } finally {
            MDC.clear();
        }
    }
    @KafkaListener(topics = KafkaTopics.PAYMENT_FAILED, groupId = "order-service")
    public void onPaymentFailed(PaymentFailedEvent event, Acknowledgment ack) {
        try {
            if (event.getCorrelationId() != null) MDC.put(CORRELATION_ID, event.getCorrelationId());
            sagaService.handlePaymentFailed(event.getEventId(), event.getOrderId(), KafkaTopics.PAYMENT_FAILED);
            ack.acknowledge();
        } catch (Exception ex) {
            //do not ack; allow retry/DLT
            throw ex;
        } finally {
            MDC.clear();
        }
    }
}
