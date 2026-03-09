package com.rtp.orderservice.db;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "orders")
public class Order {
    @Id
    private Long id;

    @Column(name = "product_name", nullable = false)
    private String productName;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private String status;

    @Column(name = "correlation_id")
    private String correlationId;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    public Order() {}

    public Order(Long id, String productName, int quantity, String status, String correlationId) {
        this.id = id;
        this.productName = productName;
        this.quantity = quantity;
        this.status = status;
        this.correlationId = correlationId;
        this.createdAt = Instant.now();
    }

    public Long getId() { return id; }
    public String getProductName() { return productName; }
    public int getQuantity() { return quantity; }
    public String getStatus() { return status; }
    public String getCorrelationId() { return correlationId; }
    public Instant getCreatedAt() { return createdAt; }

    public void setStatus(String status) { this.status = status; }
}
