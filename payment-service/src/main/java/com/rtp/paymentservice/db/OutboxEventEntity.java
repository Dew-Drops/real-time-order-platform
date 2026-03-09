package com.rtp.paymentservice.db;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "outbox_events", indexes = {
        @Index(name = "idx_outbox_status_created", columnList = "status, created_at")
})
public class OutboxEventEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="aggregate_id", nullable = false)
    private String aggregateId; // orderId as string

    @Column(name="event_type", nullable = false, length = 100)
    private String eventType; // payment.succeeded / payment.failed

    @Lob
    @Column(name="payload", nullable = false)
    private String payload; // JSON

    @Column(name="status", nullable = false, length = 20)
    private String status; // NEW / SENT / FAILED

    @Column(name="created_at", nullable = false)
    private Instant createdAt = Instant.now();

    @Column(name="sent_at")
    private Instant sentAt;

    @Column(name="correlation_id", length = 100)
    private String correlationId;

    public OutboxEventEntity() {}

    public OutboxEventEntity(String aggregateId, String eventType, String payload, String correlationId) {
        this.aggregateId = aggregateId;
        this.eventType = eventType;
        this.payload = payload;
        this.status = "NEW";
        this.createdAt = Instant.now();
        this.correlationId = correlationId;
    }

    public Long getId() { return id; }
    public String getAggregateId() { return aggregateId; }
    public String getEventType() { return eventType; }
    public String getPayload() { return payload; }
    public String getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getSentAt() { return sentAt; }
    public String getCorrelationId() { return correlationId; }

    public void markSent() {
        this.status = "SENT";
        this.sentAt = Instant.now();
    }

    public void markFailed() {
        this.status = "FAILED";
    }
}