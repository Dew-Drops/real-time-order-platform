package com.rtp.paymentservice.db;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(
        name = "payments",
        uniqueConstraints = @UniqueConstraint(name = "uk_payments_order_id", columnNames = "order_id")
)
public class PaymentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="order_id", nullable = false)
    private Long orderId;

    @Column(nullable = false)
    private String status; // SUCCEEDED / FAILED

    @Column(name="payment_id")
    private String paymentId;

    @Column(name="failure_reason")
    private String failureReason;

    @Column(name="correlation_id")
    private String correlationId;

    @Column(name="created_at", nullable = false)
    private Instant createdAt = Instant.now();

    public PaymentEntity() {}

    public PaymentEntity(Long orderId, String status, String paymentId, String failureReason, String correlationId) {
        this.orderId = orderId;
        this.status = status;
        this.paymentId = paymentId;
        this.failureReason = failureReason;
        this.correlationId = correlationId;
        this.createdAt = Instant.now();
    }

    public Long getId() { return id; }
    public Long getOrderId() { return orderId; }
    public String getStatus() { return status; }
    public String getPaymentId() { return paymentId; }
    public String getFailureReason() { return failureReason; }
    public String getCorrelationId() { return correlationId; }
}