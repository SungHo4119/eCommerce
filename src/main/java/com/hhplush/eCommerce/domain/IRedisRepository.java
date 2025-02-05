package com.hhplush.eCommerce.domain;

import org.redisson.api.RAtomicLong;
import org.redisson.api.RMap;
import org.redisson.api.RScoredSortedSet;

public interface IRedisRepository {

    // 키가 존재하는지 조회
    boolean isExists(String key);

    // Key에 대한 AtomicLong 객체 반환
    RAtomicLong getAtomicLong(String key);


    // ScoredSortedSet 값 추가
    void addScoredSortedSet(String queueKey, String issuedCouponKey);

    // ScoredSortedSet 값 조회
    RScoredSortedSet<String> getScoredSortedSet(String queueKey);

    // getMap
    RMap<String, Boolean> getMap(String key);
}
