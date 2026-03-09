package com.rtp.orderservice.kafka;

public final class KafkaTopics {
    public static final String ORDER_CREATED = "order.created";
    public static final String PAYMENT_SUCCEEDED = "payment.succeeded";
    public static final String PAYMENT_FAILED = "payment.failed";
    private KafkaTopics() {}
}