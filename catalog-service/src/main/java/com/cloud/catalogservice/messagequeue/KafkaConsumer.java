package com.cloud.catalogservice.messagequeue;

import com.cloud.catalogservice.jpa.CatalogEntity;
import com.cloud.catalogservice.jpa.CatalogRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaConsumer {

    private final CatalogRepository catalogRepository;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "example-order-topic")
    public void processMessage(String kafkaMessage) {
        log.info("Kafka Message :{}", kafkaMessage);
        Map<String, Object> map = new HashMap<>();
        try {
            map = objectMapper.readValue(kafkaMessage, new TypeReference<Map<String, Object>>() {});
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        CatalogEntity catalogEntity = catalogRepository.findByProductId((String) map.get("productId"));
        catalogEntity.setStock(catalogEntity.getStock() - (Integer) map.get("quantity"));
        // JPA save는 존재할 때 update로 실행된다.
        catalogRepository.save(catalogEntity);
    }
}
