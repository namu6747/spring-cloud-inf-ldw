package com.cloud.orderservice.vo;

import lombok.Data;

@Data
public class RequestOrder {
    private String productId;
    private Integer quantity;
    private Integer unitPrice;
}
