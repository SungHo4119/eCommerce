package com.hhplush.eCommerce.infrastructure.redis;

import static com.hhplush.eCommerce.common.utils.RedisUtil.COUPON_ISSUED_KEY;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RMap;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class RedisService {

    private final RedissonClient redissonClient;

    public RedisService(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    // 쿠폰 수량 저장
    public void addCouponQuantity(String key, Long number) {
        RAtomicLong atomicLong = redissonClient.getAtomicLong(key);
        atomicLong.set(number);
    }


    // 쿠폰 수량 조회
    public Long getCouponQuantity(String key) {
        RAtomicLong atomicCoupon = redissonClient.getAtomicLong(key);
        return atomicCoupon.get();
    }

    // 쿠폰 큐 조회
    public RScoredSortedSet<String> getSetValue(String key) {
        RScoredSortedSet<String> setValue = redissonClient.getScoredSortedSet(key);
        return setValue;
    }

    // 쿠폰 발급 이력 조회
    public Integer getCouponIssuedList(String key) {
        RMap<String, Boolean> issuedUsers = redissonClient.getMap(COUPON_ISSUED_KEY);
        return issuedUsers.size();
    }

    // 레디스의 모든 키 삭제
    public void deleteAllKeys() {
        redissonClient.getKeys().flushall();
    }
}
