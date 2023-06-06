package com.cloud.orderservice.messagequeue;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Payload {
    private String order_id;
    private String user_id;
    private String product_id;
    private Integer quantity;
    private Integer total_price;
    private Integer unit_price;
}
