package com.cloud.orderservice.messagequeue;

import java.io.Serializable;

public record KafkaOrderDto(Schema schema, Payload payload) implements Serializable {

}
