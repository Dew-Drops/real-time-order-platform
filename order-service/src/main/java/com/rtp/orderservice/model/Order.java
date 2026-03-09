package com.rtp.orderservice.model;

public class Order {

    private Long id;
    private String productName;
    private int quantity;
    private String status; // PENDING_PAYMENT / PAID / PAYMENT_FAILED
    public Order(Long id, String productName, int quantity) {
        this.id = id;
        this.productName = productName;
        this.quantity = quantity;
        this.status = "PENDING_PAYMENT";
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public String getProductName() {
        return productName;
    }

    public int getQuantity() {
        return quantity;
    }
}
