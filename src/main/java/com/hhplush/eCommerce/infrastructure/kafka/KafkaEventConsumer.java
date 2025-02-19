package com.hhplush.eCommerce.infrastructure.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class KafkaEventConsumer {

    @KafkaListener(topics = "userId", groupId = "eCommerce", containerFactory = "kafkaListenerContainerFactory")
    public void consumeRecord(ConsumerRecord<String, String> record) {
        String value = record.value();
        log.info("유저 조회 성공시 발행 된 메시지를 전달 받습니다. message : {}", value);
    }
}
