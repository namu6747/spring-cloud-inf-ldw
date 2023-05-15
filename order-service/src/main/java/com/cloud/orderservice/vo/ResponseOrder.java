package com.cloud.orderservice.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseOrder {
    private String productId;
    private Integer quantity;
    private Integer unitPrice;
    private Integer totalPrice;
    private String orderId;
    private LocalDateTime createdAt;
}
