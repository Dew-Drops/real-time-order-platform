package com.rtp.apigateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.web.server.ServerWebExchange;

import java.util.UUID;

@Configuration
public class CorrelationIdFilter {

    private static final Logger log = LoggerFactory.getLogger(CorrelationIdFilter.class);
    private static final String CORRELATION_ID = "X-Correlation-Id";

    @Bean
    @Order(-1) // run early
    public GlobalFilter correlationFilter() {
        return (exchange, chain) -> {

            String incomingCorrelationId =
                    exchange.getRequest().getHeaders().getFirst(CORRELATION_ID);

            final String correlationId =
                    (incomingCorrelationId != null && !incomingCorrelationId.isBlank())
                            ? incomingCorrelationId
                            : UUID.randomUUID().toString();

            log.info("Incoming request [{} {}] correlationId={}",
                    exchange.getRequest().getMethod(),
                    exchange.getRequest().getURI(),
                    correlationId
            );

            ServerWebExchange mutatedExchange = exchange.mutate()
                    .request(req -> req.headers(headers -> {
                        headers.remove(CORRELATION_ID);
                        headers.add(CORRELATION_ID, correlationId);
                    }))
                    .build();

            return chain.filter(mutatedExchange);
        };
    }
}
