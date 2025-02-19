package com.hhplush.eCommerce.config;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.scheduling.annotation.EnableScheduling;


@EnableKafka
@Configuration
@RequiredArgsConstructor
@EnableScheduling
public class KafkaConfig {

    @Value("${spring.kafka.producer.topic.userId}")
    private String userId;
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public NewTopic userId() {
        return TopicBuilder.name(userId)
            .partitions(3)
            .replicas(3)
            .build();
    }

    // 토픽 자동생성 ( 없으면 새로만듬 )
    @Bean
    public KafkaAdmin kafkaAdmin() {
        return new KafkaAdmin(Map.of(
            AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers
        ));
    }

}
