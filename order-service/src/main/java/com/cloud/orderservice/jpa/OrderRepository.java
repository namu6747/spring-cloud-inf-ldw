package com.cloud.orderservice.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
    OrderEntity findByOrderId(String orderId);
    List<OrderEntity> findByUserId(String userId);
}
