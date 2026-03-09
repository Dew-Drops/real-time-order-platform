package com.rtp.orderservice.db;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "processed_events")
public class ProcessedEventEntity {
    @Id
    @Column(name = "event_id", length = 100)
    private String eventId;

    @Column(name = "processed_at", nullable = false)
    private Instant processedAt;

    @Column(length = 100)
    private String consumer;

    @Column(length = 200)
    private String topic;
    public ProcessedEventEntity() {}
    public ProcessedEventEntity(String eventId, String consumer, String topic) {
        this.eventId = eventId;
        this.consumer = consumer;
        this.topic = topic;
        this.processedAt = Instant.now();
    }
    public String getEventId() { return eventId; }
}
