package com.hhplush.eCommerce.infrastructure.kafka;

import com.hhplush.eCommerce.domain.IEventPublisher;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaEventPublisher implements IEventPublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public KafkaEventPublisher(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishString(String topic, String message) {
        kafkaTemplate.send(topic, message);
    }
}
