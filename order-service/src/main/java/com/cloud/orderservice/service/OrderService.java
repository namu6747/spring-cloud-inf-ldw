package com.cloud.orderservice.service;

import com.cloud.orderservice.dto.OrderDto;
import com.cloud.orderservice.jpa.OrderEntity;

import java.util.List;

public interface OrderService {
    OrderDto createOrder(OrderDto orderDetails);
    OrderDto getOrderByOrderId(String orderId);
    List<OrderEntity> getOrdersByUserId(String userId);
}
