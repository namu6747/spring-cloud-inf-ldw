package com.cloud.orderservice.messagequeue;

import com.cloud.orderservice.dto.OrderDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    private final List<Field> fields = Arrays.asList(
            new Field("string", true, "order_id"),
            new Field("string", true, "user_id"),
            new Field("string", true, "product_id"),
            new Field("int32", true, "quantity"),
            new Field("int32", true, "total_price"),
            new Field("int32", true, "unit_price")
    );
    private final Schema schema = Schema.builder()
                                        .type("struct")
                                        .name("orders")
                                        .optional(false)
                                        .fields(fields)
                                        .build();

    public OrderDto send(String kafkaTopic, OrderDto orderDto) {
        Payload payload = Payload.builder()
                                .order_id(orderDto.getOrderId())
                                .user_id(orderDto.getUserId())
                                .product_id(orderDto.getProductId())
                                .quantity(orderDto.getQuantity())
                                .total_price(orderDto.getTotalPrice())
                                .unit_price(orderDto.getUnitPrice())
                                .build();

        KafkaOrderDto kafkaOrderDto = new KafkaOrderDto(schema, payload);
        String jsonInString = null;
        try {
            jsonInString = objectMapper.writeValueAsString(kafkaOrderDto);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        kafkaTemplate.send(kafkaTopic, jsonInString);
        log.info("Order Producer send Data from the Order service :{}", jsonInString);
        return orderDto;
    }

}
