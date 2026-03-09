package com.rtp.orderservice.service;

import com.rtp.orderservice.db.OrderRepository;
import com.rtp.orderservice.db.Order;
import com.rtp.orderservice.event.OrderCreatedEvent;
import com.rtp.orderservice.kafka.OrderEventProducer;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class OrderService {
    private final OrderEventProducer orderEventProducer;
    private final OrderRepository orderRepository;

    private final AtomicLong idGenerator = new AtomicLong(1);

    public OrderService(OrderEventProducer orderEventProducer, OrderRepository orderRepository) {
        this.orderEventProducer = orderEventProducer;
        this.orderRepository = orderRepository;
    }

    public Order createOrder(String productName, int quantity, String correlationId) {

        Long id = idGenerator.incrementAndGet();

        if (correlationId == null || correlationId.isBlank()) {
            correlationId = UUID.randomUUID().toString();
        }

        Order order = new Order(id, productName, quantity, "PENDING_PAYMENT", correlationId);
        orderRepository.save(order);

        OrderCreatedEvent event = new OrderCreatedEvent();
        event.setOrderId(order.getId());
        event.setProductName(order.getProductName());
        event.setQuantity(order.getQuantity());

        event.setEventId(UUID.randomUUID().toString());
        event.setCorrelationId(correlationId);
        event.setTimestamp(Instant.now());
        event.setSource("order-service");
        event.setVersion(1);
        orderEventProducer.publishOrderCreated(event);
        return order;
    }

//    public void markPaid(Long orderId) {
//        Order order = store.get(orderId);
//        if (order != null && !"PAID".equals(order.getStatus())) {//if payment service put duplicate event which is already paid, then ignore it.
//            order.setStatus("PAID");
//            store.put(orderId, order);
//        }
//    }
//
//    public void markPaymentFailed(Long orderId) {
//        Order order = store.get(orderId);
//        if (order != null && !"PAYMENT_FAILED".equals(order.getStatus())) {//if payment service put duplicate event which is already payment-failed, then ignore it.
//            order.setStatus("PAYMENT_FAILED");
//            store.put(orderId, order);
//        }
//    }
    public Order getOrder(Long id) {
        return orderRepository.findById(id).orElse(null);
    }
}
