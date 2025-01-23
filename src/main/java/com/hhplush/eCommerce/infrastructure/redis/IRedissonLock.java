package com.hhplush.eCommerce.infrastructure.redis;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface IRedissonLock {

    // 락을 구분할 key
    String key();

    // 락 획득 대기 시간
    long waitTime() default 5L;

    // 락 해제 시간
    long leaseTime() default 3L;

    // 시간 단위
    TimeUnit timeUnit() default TimeUnit.SECONDS;

}