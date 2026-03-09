package com.rtp.notificationservice.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Component
public class OrderCreatedDltConsumer {

    private static final Logger log =
            LoggerFactory.getLogger(OrderCreatedDltConsumer.class);

    @KafkaListener(
            topics = "order.created.notification.DLT",
            groupId = "notification-dlt-group",
            containerFactory = "dltKafkaListenerContainerFactory"
    )
    public void onDlt(String rawJson,
                      @Header(value = KafkaHeaders.RECEIVED_KEY, required = false) String key) {
        log.error("DLT received message key={}, payload={}", key, rawJson);
    }
}
