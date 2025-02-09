package com.hhplush.eCommerce.config;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;

@Slf4j
@Configuration
@EnableCaching
public class RedisConfiguration {

    private static final String REDISSON_HOST_PREFIX = "redis://";
    @Value("${spring.data.redis.host}")
    private String redisHost;
    @Value("${spring.data.redis.port}")
    private int redisPort;

    @Bean
    public RedissonClient redissonClient() {
        // 단일 노드
        Config config = new Config();
        config.useSingleServer().setAddress(REDISSON_HOST_PREFIX + redisHost + ":" + redisPort);
        return Redisson.create(config);
    }


    @Bean
    // 캐시 매니저 설정
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofMinutes(10))
            .disableCachingNullValues(); // null값은 캐시에 저장 안함

        RedisCacheManager cacheManager = RedisCacheManager.builder(connectionFactory)
            .cacheDefaults(redisCacheConfiguration)
            .build();
        return cacheManager;
    }

    @Bean
    public KeyGenerator localDateKeyGenerator() {
        return (target, method, params) -> Arrays.stream(params)
            .map(param -> param instanceof LocalDate ? param.toString() : param)
            .reduce((a, b) -> a + "_" + b)
            .orElse("default_key");
    }
}