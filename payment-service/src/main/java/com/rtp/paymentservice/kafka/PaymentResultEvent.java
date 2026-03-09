package com.rtp.paymentservice.kafka;

public class PaymentResultEvent {

    private Long orderId;
    private String status;
    private String reason;

    public PaymentResultEvent() {}

    public PaymentResultEvent(Long orderId, String status, String reason) {
        this.orderId = orderId;
        this.status = status;
        this.reason = reason;
    }

    public Long getOrderId() { return orderId; }
    public String getStatus() { return status; }
    public String getReason() { return reason; }
}
