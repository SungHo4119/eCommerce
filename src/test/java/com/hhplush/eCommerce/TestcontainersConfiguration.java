package com.hhplush.eCommerce;

import groovy.util.logging.Slf4j;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

@Configuration
@Slf4j
class TestcontainersConfiguration {

    public static final MySQLContainer<?> MYSQL_CONTAINER;

    // Redis 컨테이너
    public static final GenericContainer<?> REDIS_CONTAINER;
    public static final GenericContainer<?> KAFKA_CONTAINER;
    public static final Network network = Network.newNetwork();
    private static final Logger log = LoggerFactory.getLogger(TestcontainersConfiguration.class);

    static {
        MYSQL_CONTAINER = new MySQLContainer<>(DockerImageName.parse("mysql:8.0"))
            .withDatabaseName("hhplus")
            .withUsername("application")
            .withPassword("application");
        MYSQL_CONTAINER.start();

        System.setProperty("spring.datasource.url",
            MYSQL_CONTAINER.getJdbcUrl() + "?characterEncoding=UTF-8&serverTimezone=UTC");
        System.setProperty("spring.datasource.username", MYSQL_CONTAINER.getUsername());
        System.setProperty("spring.datasource.password", MYSQL_CONTAINER.getPassword());

        // Redis 설정
        REDIS_CONTAINER = new GenericContainer<>(DockerImageName.parse("redis:7.4.2"))
            .withExposedPorts(6379);  // Redis 기본 포트
        REDIS_CONTAINER.start();

        // Spring Redis 프로퍼티 설정
        System.setProperty("spring.data.redis.host", REDIS_CONTAINER.getHost());
        System.setProperty("spring.data.redis.port",
            REDIS_CONTAINER.getMappedPort(6379).toString());

        // Kafka 컨테이너 설정 (Bitnami Kafka)
        KAFKA_CONTAINER = new GenericContainer<>(DockerImageName.parse("bitnami/kafka:3.9.0"))
            .withExposedPorts(9092, 9093, 9094)
            .withNetwork(network)
            .withNetworkAliases("kafka")
            .withEnv("KAFKA_ENABLE_KRAFT", "yes")
            .withEnv("KAFKA_CFG_NODE_ID", "1")
            .withEnv("KAFKA_KRAFT_CLUSTER_ID", "local")
            .withEnv("KAFKA_CFG_PROCESS_ROLES", "controller,broker")
            .withEnv("KAFKA_CFG_LISTENERS", "PLAINTEXT://:9092,CONTROLLER://:9093")
            .withEnv("KAFKA_CFG_ADVERTISED_LISTENERS",
                "PLAINTEXT://localhost:9092")
            .withEnv("KAFKA_CFG_LISTENER_SECURITY_PROTOCOL_MAP",
                "PLAINTEXT:PLAINTEXT,CONTROLLER:PLAINTEXT")
            .withEnv("KAFKA_CFG_CONTROLLER_QUORUM_VOTERS", "1@localhost:9093")
            .withEnv("KAFKA_CFG_CONTROLLER_LISTENER_NAMES", "CONTROLLER")
            .withEnv("KAFKA_CREATE_TOPICS", "userId:1:1")
            .withEnv("KAFKA_CREATE_TOPICS", "payment-events:1:1")
            .withEnv("ALLOW_PLAINTEXT_LISTENER", "yes")
            .waitingFor(Wait.forLogMessage(".*Kafka Server started.*\\n", 1));

        KAFKA_CONTAINER.setPortBindings(List.of("9092:9092"));

        KAFKA_CONTAINER.start();

    }

//   Spring 켄텍스트가 종료되기전 PreDestroy 메소드 호출로 인해 컨테이너 종료로직 제거
//   테스트 종료시 testcontainers로 생성된 컨테이너는 자동으로 종료됨 ( 코치님 피드백 - 확인 완료 )
//    @PreDestroy
//    public void preDestroy() {
//        if (MYSQL_CONTAINER.isRunning()) {
//            log.info("MySQL 컨테이너 종료");
//            MYSQL_CONTAINER.stop();
//        }
//
//        // Redis 컨테이너 종료
//        if (REDIS_CONTAINER.isRunning()) {
//            log.info("Redis 컨테이너 종료");
//            REDIS_CONTAINER.stop();
//        }
//    }
}