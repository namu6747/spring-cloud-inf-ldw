package com.cloud.orderservice.service;

import com.cloud.orderservice.dto.OrderDto;
import com.cloud.orderservice.jpa.OrderEntity;
import com.cloud.orderservice.jpa.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    @Override
    public OrderDto createOrder(OrderDto orderDetails) {
        orderDetails.setOrderId(UUID.randomUUID().toString());
        orderDetails.setTotalPrice(orderDetails.getQuantity() * orderDetails.getUnitPrice());
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        OrderEntity orderEntity = modelMapper.map(orderDetails, OrderEntity.class);
        orderRepository.save(orderEntity);
        return modelMapper.map(orderEntity, OrderDto.class);
    }

    @Override
    public OrderDto getOrderByOrderId(String orderId) {
        ModelMapper modelMapper = new ModelMapper();
        OrderEntity findOrderEntity = orderRepository.findByOrderId(orderId);
        OrderDto orderDto = modelMapper.map(findOrderEntity, OrderDto.class);
        return orderDto;
    }

    @Override
    public List<OrderEntity> getOrdersByUserId(String userId) {
        return orderRepository.findByUserId(userId);
    }
}
