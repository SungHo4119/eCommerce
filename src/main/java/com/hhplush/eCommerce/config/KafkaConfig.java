package com.hhplush.eCommerce.config;

import java.util.Map;
import java.util.concurrent.ExecutionException;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.admin.TopicListing;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.scheduling.annotation.EnableScheduling;


@EnableKafka
@Configuration
@EnableScheduling
public class KafkaConfig {

    private final KafkaAdmin kafkaAdmin;
    @Value("${spring.kafka.producer.topic.userId}")
    private String userId;
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    public KafkaConfig(KafkaAdmin kafkaAdmin) {
        this.kafkaAdmin = kafkaAdmin;
    }

    @Bean
    public NewTopic userIdTopic() {
        try (AdminClient adminClient = AdminClient.create(
            Map.of(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers))) {
            Map<String, TopicListing> topics = adminClient.listTopics().namesToListings().get();
            if (topics.containsKey(userId)) {
                System.out.println("Topic already exists: " + userId);
                return null;  // 이미 존재하면 생성하지 않음
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        System.out.println("Creating new topic: " + userId);
        return new NewTopic(userId, 3, (short) 2);
    }
}
