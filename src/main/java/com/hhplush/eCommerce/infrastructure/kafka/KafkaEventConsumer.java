package com.hhplush.eCommerce.infrastructure.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class KafkaEventConsumer {


    private final KafkaListenerEndpointRegistry registry;

    public KafkaEventConsumer(KafkaListenerEndpointRegistry registry) {
        this.registry = registry;
    }

    // ApplicationReadyEvent 발생 후 리스너를 수동으로 시작
    @EventListener(ApplicationReadyEvent.class)
    public void startKafkaListeners() {
        registry.getListenerContainer("userListener").start();
        log.info("Kafka Listener Started!");
    }

    @KafkaListener(id = "userListener", topics = "userId", groupId = "eCommerce", containerFactory = "kafkaListenerContainerFactory", autoStartup = "false")
    public void consumeRecord(ConsumerRecord<String, String> record) {
        String value = record.value();
        log.info("유저 조회 성공시 발행 된 메시지를 전달 받습니다. message : {}", value);
    }
}
