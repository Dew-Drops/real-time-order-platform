package com.rtp.orderservice.dto;

public class CreateOrderRequest {

    private String productName;
    private int quantity;

    public String getProductName() {
        return productName;
    }

    public int getQuantity() {
        return quantity;
    }
}
