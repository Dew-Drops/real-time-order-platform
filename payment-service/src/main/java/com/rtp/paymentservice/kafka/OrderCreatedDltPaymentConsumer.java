package com.rtp.paymentservice.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Component
public class OrderCreatedDltPaymentConsumer {

    private static final Logger log =
            LoggerFactory.getLogger(OrderCreatedDltPaymentConsumer.class);

    @KafkaListener(
            topics = "order.created.payment.DLT",
            groupId = "payment-dlt-group",
            containerFactory = "dltKafkaListenerContainerFactory"
    )
    public void onDlt(String rawJson,
                      @Header(value = KafkaHeaders.RECEIVED_KEY, required = false) String key) {
        log.error("Payment Consumer DLT received message key={}, payload={}", key, rawJson);
    }
}

