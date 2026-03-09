package com.rtp.orderservice.event;


import java.time.Instant;

public class OrderCreatedEvent {

    // ---- saga / tracing metadata ----
    private String eventId;        // UUID
    private String correlationId;  // saga/trace id
    private Instant timestamp;
    private String source;         // "order-service"
    private int version;           // 1

    // ---- business fields ----
    private Long orderId;
    private String productName;
    private int quantity;

    public OrderCreatedEvent() {}

    // ---- getters/setters (metadata) ----
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

    // ---- getters/setters (business) ----
    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    @Override
    public String toString() {
        return "OrderCreatedEvent{" +
                "eventId='" + eventId + '\'' +
                ", correlationId='" + correlationId + '\'' +
                ", timestamp=" + timestamp +
                ", source='" + source + '\'' +
                ", version=" + version +
                ", orderId=" + orderId +
                ", productName='" + productName + '\'' +
                ", quantity=" + quantity +
                '}';
    }
}

