package com.rtp.orderservice.controller;

import com.rtp.orderservice.dto.CreateOrderRequest;
import com.rtp.orderservice.db.Order;
import com.rtp.orderservice.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/orders")
public class OrderController {
    private static final Logger log =
            LoggerFactory.getLogger(OrderController.class);
    private final OrderService orderService;
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public Order createOrder(
            @RequestBody CreateOrderRequest createOrderRequest,
            @RequestHeader(value = "X-Correlation-Id", required = false) String correlationId
    ) {
        return orderService.createOrder(
                createOrderRequest.getProductName(),
                createOrderRequest.getQuantity(),
                correlationId
        );
    }
//    @PostMapping
//    public ResponseEntity<Order> createOrder(@RequestBody Order order) {
//        return ResponseEntity.ok(orderService.createOrder(order));
//    }
    @GetMapping("/{id}")
    public Order getOrder(@PathVariable Long id)
    {
        return orderService.getOrder(id);
    }
    @GetMapping("/health")
    public String health() {
        return "Order Service is UP and running";
    }

    @GetMapping("/fail")
    public ResponseEntity<String> fail(HttpServletRequest request) {

        String correlationId = request.getHeader("X-Correlation-Id");
        log.info("Correlation ID received in controller: {}", correlationId);

        throw new RuntimeException("Simulated failure");
    }

}
