package com.rtp.orderservice.kafka;

import com.rtp.orderservice.event.OrderCreatedEvent;
import org.slf4j.MDC;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

//import static com.rtp.orderservice.filter.CorrelationIdFilter.CORRELATION_ID;
import static org.springframework.kafka.support.KafkaHeaders.CORRELATION_ID;


@Service
public class OrderEventProducer {

    private final KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate;

    public OrderEventProducer(KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishOrderCreated(OrderCreatedEvent event) {
        String correlationId = MDC.get(CORRELATION_ID);

        var message = MessageBuilder
                .withPayload(event)
                .setHeader(KafkaHeaders.TOPIC, KafkaTopics.ORDER_CREATED)
                .setHeader(KafkaHeaders.KEY, String.valueOf(event.getOrderId()))
                // optional but very useful: propagate correlationId to consumers via headers
                .setHeader(CORRELATION_ID, correlationId)//optional because correlationId is already present in event.
                .build();

        kafkaTemplate.send(message);
    }
}
