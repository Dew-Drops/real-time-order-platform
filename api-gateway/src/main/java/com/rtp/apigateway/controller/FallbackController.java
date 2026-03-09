package com.rtp.apigateway.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import org.springframework.http.server.reactive.ServerHttpRequest;
import reactor.core.publisher.Mono;
@RestController
@RequestMapping("/fallback")
public class FallbackController {
    private static final Logger log =
            LoggerFactory.getLogger(FallbackController.class);
    @GetMapping("/orders")
    public Mono<ResponseEntity<String>> orderFallback(ServerHttpRequest request) {
        String correlationId = request.getHeaders().getFirst("X-Correlation-Id");

        log.info("Fallback triggered for Order Service, correlationId={}", correlationId);

        return Mono.just(
                ResponseEntity.status(200)
                        .header("X-Correlation-Id", correlationId != null ? correlationId : "")
                        .header("X-Resilience-Result", "FALLBACK")
                        .body("Order Service unavailable")
        );
    }

}
