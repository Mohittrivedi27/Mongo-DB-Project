package com.jobautomation.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaEventProducer {

    private static final Logger log = LoggerFactory.getLogger(KafkaEventProducer.class);

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public KafkaEventProducer(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public void publishEvent(String topic, ApplicationEventPayload payload) {
        try {
            String jsonPayload = objectMapper.writeValueAsString(payload);
            kafkaTemplate.send(topic, payload.getApplicationId(), jsonPayload);
            log.info("Published Kafka event to topic [{}] for application [{}]", topic, payload.getApplicationId());
        } catch (Exception ex) {
            log.warn("Could not publish to Kafka topic [{}] (asynchronous fallback active): {}", topic, ex.getMessage());
        }
    }
}
