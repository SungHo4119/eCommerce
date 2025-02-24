package com.hhplush.eCommerce.infrastructure.kafka;

import com.hhplush.eCommerce.domain.event.ProcessState;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaEventConsumer {


    private final KafkaListenerEndpointRegistry registry;
    private final IOutboxEventJPARepository outboxEventJPARepository;


    // ApplicationReadyEvent 발생 후 리스너를 수동으로 시작
    @EventListener(ApplicationReadyEvent.class)
    public void startKafkaListeners() {
        registry.getListenerContainer("userListener").start();
        registry.getListenerContainer("paymentListener").start();
        log.info("Kafka Listener Started!");
    }

    @KafkaListener(id = "userListener", topics = "userId", groupId = "eCommerce", containerFactory = "kafkaListenerContainerFactory", autoStartup = "false")
    public void consumeRecord(ConsumerRecord<String, String> record) {
        String value = record.value();
        log.info("유저 조회 성공시 발행 된 메시지를 전달 받습니다. message : {}", value);
    }

    @KafkaListener(id = "paymentListener", topics = "payment-events", groupId = "eCommerce", containerFactory = "kafkaListenerContainerFactory", autoStartup = "false")
    public void consumePaymentRecord(ConsumerRecord<String, String> record) {
        String value = record.value();
        log.info("결제 완료시 발행 된 메시지를 전달 받습니다.  payment-events : {}", value);
        outboxEventJPARepository.findById(Long.valueOf(value)).ifPresent(outboxEvent -> {
            outboxEvent.setProcessState(ProcessState.PROCESSED);
            outboxEventJPARepository.save(outboxEvent);
        });

    }
}
