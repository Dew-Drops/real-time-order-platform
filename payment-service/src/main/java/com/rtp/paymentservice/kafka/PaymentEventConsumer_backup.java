//package com.rtp.paymentservice.kafka;
//
//import com.rtp.paymentservice.db.PaymentEntity;
//import com.rtp.paymentservice.db.PaymentRepository;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.slf4j.MDC;
//import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.kafka.support.Acknowledgment;
//import org.springframework.kafka.support.KafkaHeaders;
//import org.springframework.messaging.handler.annotation.Header;
//import org.springframework.stereotype.Component;
//
//import java.util.Random;
//import java.util.UUID;
//
//@Component
//public class PaymentEventConsumer_backup {
//    private static final Logger log = LoggerFactory.getLogger(PaymentEventConsumer_backup.class);
//    private static final String CORRELATION_ID = "X-Correlation-Id";
//
//    private final Random random = new Random();
//    private final PaymentEventProducer paymentEventProducer;
//    private final PaymentRepository paymentRepository;
//
//    public PaymentEventConsumer_backup(PaymentEventProducer paymentEventProducer, PaymentRepository paymentRepository) {
//        this.paymentEventProducer = paymentEventProducer;
//        this.paymentRepository = paymentRepository;
//    }
//
//    @KafkaListener(topics = KafkaTopics.ORDER_CREATED, groupId = "payment-service-group")
//    public void onOrderCreated(
//            OrderCreatedEvent event,
//            @Header(value = CORRELATION_ID, required = false) String correlationId,
//            @Header(value = KafkaHeaders.RECEIVED_KEY, required = false) String key,
//            Acknowledgment ack
//    ) {
//        if (correlationId != null && !correlationId.isBlank()) {
//            MDC.put(CORRELATION_ID, correlationId);
//        }
//        try {
//            log.info("Payment Service received event. key={}, event={}", key, event);
//            //Idempotency: if already processed this orderId, do not decide again
//            var existing = paymentRepository.findByOrderId(event.getOrderId());
//            if (existing.isPresent()) {
//                PaymentEntity p = existing.get();
//                log.info("Duplicate order.created for orderId={} detected. Returning stored status={}",
//                        p.getOrderId(), p.getStatus());
//                //Optional but recommended: re-publish stored outcome (helps if downstream missed it)
//                if ("SUCCEEDED".equals(p.getStatus())) {
//                    paymentEventProducer.publishPaymentSucceeded(event, p.getPaymentId());
//                } else {
//                    paymentEventProducer.publishPaymentFailed(event,
//                            p.getFailureReason() != null ? p.getFailureReason() : "FAILED");
//                }
//                ack.acknowledge();
//                return;
//            }
//            boolean success = random.nextInt(100) < 70;
//            if (success) {
//                String paymentId = UUID.randomUUID().toString();
//                paymentRepository.save(new PaymentEntity(
//                        event.getOrderId(),
//                        "SUCCEEDED",
//                        paymentId,
//                        null,
//                        event.getCorrelationId()
//                ));
//                paymentEventProducer.publishPaymentSucceeded(event, paymentId);
//                log.info("Payment succeeded for orderId={}", event.getOrderId());
//            } else {
//                String reason = "Insufficient balance";
//                paymentRepository.save(new PaymentEntity(
//                        event.getOrderId(),
//                        "FAILED",
//                        null,
//                        reason,
//                        event.getCorrelationId()
//                ));
//                paymentEventProducer.publishPaymentFailed(event, reason);
//                log.warn("Payment failed for orderId={}", event.getOrderId());
//            }
//            ack.acknowledge();
//        } catch (Exception ex) {
//            log.error("Error processing payment for orderId={}", event.getOrderId(), ex);
//            throw ex; // retry / DLT
//        } finally {
//            MDC.remove(CORRELATION_ID);
//        }
//    }
//}