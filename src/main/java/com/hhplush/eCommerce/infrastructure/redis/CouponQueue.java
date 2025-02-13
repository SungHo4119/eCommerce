package com.hhplush.eCommerce.infrastructure.redis;

import com.hhplush.eCommerce.domain.ICouponQueue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RMap;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CouponQueue implements ICouponQueue {

    private final RedissonClient redissonClient;

    @Override
    public boolean isExists(String key) {
        return redissonClient.getBucket(key).isExists();
    }

    @Override
    public RAtomicLong getAtomicLong(String key) {
        return redissonClient.getAtomicLong(key);
    }

    @Override
    public void addScoredSortedSet(String queueKey, String issuedCouponKey) {
        RScoredSortedSet<String> couponQueue = redissonClient.getScoredSortedSet(queueKey);
        // 쿠폰 발급 대기열에 추가
        log.info("쿠폰 발급 대기열 추가: key = {}", issuedCouponKey);
        couponQueue.add(System.currentTimeMillis(), issuedCouponKey);
    }

    @Override
    public RScoredSortedSet<String> getScoredSortedSet(String queueKey) {
        return redissonClient.getScoredSortedSet(queueKey);
    }

    @Override
    public RMap<String, Boolean> getMap(String key) {
        return redissonClient.getMap(key);
    }

}
