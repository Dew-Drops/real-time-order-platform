package com.rtp.orderservice.event;

import java.time.Instant;

public class PaymentSucceededEvent {
    private String eventId;
    private String correlationId;
    private Instant timestamp;
    private String source;
    private int version;

    private Long orderId;
    private String paymentId;

    public PaymentSucceededEvent() {}

    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }

    public String getCorrelationId() { return correlationId; }
    public void setCorrelationId(String correlationId) { this.correlationId = correlationId; }

    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public int getVersion() { return version; }
    public void setVersion(int version) { this.version = version; }

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public String getPaymentId() { return paymentId; }
    public void setPaymentId(String paymentId) { this.paymentId = paymentId; }
}

