package com.rtp.paymentservice.db;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OutboxEventRepository extends JpaRepository<OutboxEventEntity, Long> {

    List<OutboxEventEntity> findByStatusOrderByCreatedAtAsc(String status, Pageable pageable);
}