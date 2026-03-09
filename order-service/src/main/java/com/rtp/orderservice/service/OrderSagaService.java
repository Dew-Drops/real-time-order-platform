package com.rtp.orderservice.service;
import com.rtp.orderservice.db.Order;
import com.rtp.orderservice.db.OrderRepository;
import com.rtp.orderservice.db.ProcessedEventEntity;
import com.rtp.orderservice.db.ProcessedEventRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
public class OrderSagaService {
    private final OrderRepository orderRepository;
    private final ProcessedEventRepository processedEventRepository;
    public OrderSagaService(OrderRepository orderRepository, ProcessedEventRepository processedEventRepository) {
        this.orderRepository = orderRepository;
        this.processedEventRepository = processedEventRepository;
    }
    @Transactional
    public void handlePaymentSucceeded(String eventId, Long orderId, String topic) {
        if (eventId == null || eventId.isBlank()) {
            throw new IllegalArgumentException("eventId is required for idempotency");
        }
        if (!tryMarkProcessed(eventId, topic)) return; // duplicate -> ignore
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order != null) order.setStatus("PAID");
    }
    @Transactional
    public void handlePaymentFailed(String eventId, Long orderId, String topic) {
        if (eventId == null || eventId.isBlank()) {
            throw new IllegalArgumentException("eventId is required for idempotency");
        }
        if (!tryMarkProcessed(eventId, topic)) return; // duplicate -> ignore
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order != null) order.setStatus("PAYMENT_FAILED");
    }
    private boolean tryMarkProcessed(String eventId, String topic) {
        try {
            processedEventRepository.save(new ProcessedEventEntity(eventId, "order-service", topic));
            return true;
        } catch (DataIntegrityViolationException dup) {
            return false; // event_id PK already exists
        }
    }
}
