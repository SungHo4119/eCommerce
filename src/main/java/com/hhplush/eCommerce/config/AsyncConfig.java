package com.hhplush.eCommerce.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

@Configuration
@EnableAsync
public class AsyncConfig {
    // 이벤트 리스너에서 @Async 어노테이션을 사용하기 위해 설정
}
