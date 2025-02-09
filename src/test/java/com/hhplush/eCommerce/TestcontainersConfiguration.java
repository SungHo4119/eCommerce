package com.hhplush.eCommerce;

import groovy.util.logging.Slf4j;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.utility.DockerImageName;

@Configuration
@Slf4j
class TestcontainersConfiguration {

    public static final MySQLContainer<?> MYSQL_CONTAINER;

    // Redis 컨테이너
    public static final GenericContainer<?> REDIS_CONTAINER;
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
    }

    @PreDestroy
    public void preDestroy() {
        if (MYSQL_CONTAINER.isRunning()) {
            log.info("MySQL 컨테이너 종료");
            MYSQL_CONTAINER.stop();
        }

        // Redis 컨테이너 종료
        if (REDIS_CONTAINER.isRunning()) {
            log.info("Redis 컨테이너 종료");
            REDIS_CONTAINER.stop();
        }
    }
}