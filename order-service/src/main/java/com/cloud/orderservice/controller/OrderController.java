package com.cloud.orderservice.controller;

import com.cloud.orderservice.dto.OrderDto;
import com.cloud.orderservice.messagequeue.KafkaProducer;
import com.cloud.orderservice.service.OrderService;
import com.cloud.orderservice.vo.RequestOrder;
import com.cloud.orderservice.vo.ResponseOrder;
import com.cloud.orderservice.vo.ServerInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.Inet4Address;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/")
public class OrderController {

    private final OrderService orderService;
    private final Environment environment;
    private final KafkaProducer kafkaProducer;

    @GetMapping("/health_check")
    public ServerInfo status() throws Exception {
        String[] activeProfiles = environment.getActiveProfiles();
        for (String activeProfile : activeProfiles) {
            log.info("active {}:[{}]", activeProfile, environment.getProperty(activeProfile));
        }
        String[] defaultProfiles = environment.getDefaultProfiles();
        for (String defaultProfile : defaultProfiles) {
            log.info("default {}:[{}]", defaultProfile, environment.getProperty(defaultProfile));
        }
        ServerInfo serverInfoDto = new ServerInfo();
        serverInfoDto.setHost(Inet4Address.getLocalHost().getHostAddress());
        serverInfoDto.setPort(environment.getProperty("local.server.port"));
        return serverInfoDto;
    }

    @GetMapping("/{userId}/orders")
    public ResponseEntity<List<ResponseOrder>> findOrderAll(@PathVariable("userId") String userId) {
        ModelMapper modelMapper = new ModelMapper();
        List<ResponseOrder> responseOrders = orderService.getOrdersByUserId(userId)
                                                         .stream()
                                                         .map(e -> modelMapper.map(e, ResponseOrder.class))
                                                         .collect(Collectors.toList());
        return ResponseEntity.ok(responseOrders);
    }

    @PostMapping("/{userId}/orders")
    public ResponseEntity<ResponseOrder> createOrder(@PathVariable("userId") String userId,
                                                     @RequestBody RequestOrder orderDetails) {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        OrderDto orderDto = modelMapper.map(orderDetails, OrderDto.class);
        orderDto.setUserId(userId);
        OrderDto createdOrder = orderService.createOrder(orderDto);

        ResponseOrder responseOrder = modelMapper.map(createdOrder, ResponseOrder.class);

        kafkaProducer.send("example-order-topic", orderDto);

        return ResponseEntity.created(URI.create("/orders/" + createdOrder.getOrderId())).body(responseOrder);
    }

    @GetMapping("/orders/{id}")
    public ResponseEntity<ResponseOrder> findOrder(@PathVariable("id") String id) {
        ModelMapper modelMapper = new ModelMapper();
        OrderDto findOrder = orderService.getOrderByOrderId(id);
        ResponseOrder responseOrder = modelMapper.map(findOrder, ResponseOrder.class);
        return ResponseEntity.ok(responseOrder);
    }


}
