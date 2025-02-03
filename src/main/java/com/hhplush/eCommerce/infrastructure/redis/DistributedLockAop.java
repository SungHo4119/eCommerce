package com.hhplush.eCommerce.infrastructure.redis;


import java.lang.reflect.Method;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;


@Aspect
@Component
@Order(1)
@RequiredArgsConstructor
@Slf4j
public class DistributedLockAop {

    private static final String REDISSON_LOCK_PREFIX = "LOCK:";

    private final RedissonClient redissonClient;

    @Around("@annotation(com.hhplush.eCommerce.infrastructure.redis.DistributedLock) && args(targetId,..)")
    public Object aroundLock(ProceedingJoinPoint joinPoint, Long targetId)
        throws Throwable {

        DistributedLock redissonLock = getAnnotation(joinPoint);

        String lockName = getLockName(targetId, redissonLock);
        log.info("lockName: {}", lockName);

        RLock lock = redissonClient.getLock(REDISSON_LOCK_PREFIX + lockName);
        boolean available = false;

        try {
            // waitTime 동안 대기하고 leaseTime 동안 락을 유지 ( 분산락 )
            available = lock.tryLock(redissonLock.waitTime(), redissonLock.leaseTime(),
                redissonLock.timeUnit());
            if (!available) {
                log.warn("Failed to acquire lock: {}", lockName);
                throw new IllegalArgumentException();
            }
            log.info("Successfully acquired lock: {}", lockName);

            // 락 획득 성공 시 비즈니스 로직 수행
            return joinPoint.proceed();

        } finally {
            try {
                lock.unlock();
            } catch (IllegalMonitorStateException e) {
                log.error("Failed to unlock the lock: {}", lockName);
            }
        }
    }

    private DistributedLock getAnnotation(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        return method.getAnnotation(DistributedLock.class);
    }

    private String getLockName(Long targetId, DistributedLock redissonLock) {
        String lockNameFormat = "lock:%s:%s";
        String relevantParameter = targetId.toString();
        return String.format(lockNameFormat, redissonLock.key(), relevantParameter);
    }
}