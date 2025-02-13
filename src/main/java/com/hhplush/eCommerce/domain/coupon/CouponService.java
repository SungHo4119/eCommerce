package com.hhplush.eCommerce.domain.coupon;

import static com.hhplush.eCommerce.common.exception.message.ExceptionMessage.COUPON_ALREADY_EXISTS;
import static com.hhplush.eCommerce.common.exception.message.ExceptionMessage.COUPON_LIMIT_EXCEEDED;
import static com.hhplush.eCommerce.common.exception.message.ExceptionMessage.COUPON_NOT_FOUND;
import static com.hhplush.eCommerce.common.exception.message.ExceptionMessage.COUPON_USE_ALREADY_EXISTS;
import static com.hhplush.eCommerce.common.utils.RedisUtil.COUPON_ISSUED_KEY;
import static com.hhplush.eCommerce.common.utils.RedisUtil.COUPON_KEY;
import static com.hhplush.eCommerce.common.utils.RedisUtil.COUPON_QUEUE_KEY;
import static com.hhplush.eCommerce.common.utils.RedisUtil.POLL_SIZE;
import static com.hhplush.eCommerce.common.utils.RedisUtil.getCouponKey;
import static com.hhplush.eCommerce.common.utils.RedisUtil.getIssuedCouponKey;

import com.hhplush.eCommerce.common.exception.custom.AlreadyExistsException;
import com.hhplush.eCommerce.common.exception.custom.LimitExceededException;
import com.hhplush.eCommerce.common.exception.custom.ResourceNotFoundException;
import com.hhplush.eCommerce.domain.ICouponQueue;
import com.hhplush.eCommerce.domain.user.User;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RMap;
import org.redisson.api.RScoredSortedSet;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CouponService {

    private final ICouponRepository couponRepository;
    private final ICouponQueue redisRepository;

    // 쿠폰 정보 조회
    public Coupon getCouponByCouponId(Long couponId) {
        return couponRepository.findById(couponId)
            .orElseThrow(() -> new ResourceNotFoundException(COUPON_NOT_FOUND));
    }


    // 쿠폰 수량 체크
    public CouponQuantity checkCouponQuantityWithLock(Long couponId) {
        CouponQuantity couponQuantity = couponRepository.findCouponQuantityByCouponIdWithLock(
            couponId);
        if (couponQuantity.isValidQuantity()) {
            throw new LimitExceededException(COUPON_LIMIT_EXCEEDED);
        }
        return couponQuantity;
    }

    public CouponQuantity checkCouponQuantity(Long couponId) {
        CouponQuantity couponQuantity = couponRepository.findCouponQuantityByCouponId(
            couponId);
        if (couponQuantity.isValidQuantity()) {
            throw new LimitExceededException(COUPON_LIMIT_EXCEEDED);
        }
        return couponQuantity;
    }

    // 쿠폰 발급
    public UserCoupon issueUserCoupon(Coupon coupon, CouponQuantity couponQuantity, User user) {
        UserCoupon userCoupon = new UserCoupon(coupon, user.getUserId());
        couponRepository.userCouponSave(userCoupon);

        // 재고 감소
        couponQuantity.decreaseCouponCount();
        couponRepository.couponQuantitySave(couponQuantity);
        return userCoupon;
    }

    // 이미 발급 받은 쿠폰인지 체크
    public void checkCouponValidity(Long userId, Long couponId) {
        Optional<UserCoupon> userCoupon = couponRepository.findByUserIdAndCouponId(userId,
            couponId);

        // 쿠폰이 존재한다면 이미 발급 받은 쿠폰이므로 예외 처리
        if (userCoupon.isPresent()) {
            throw new AlreadyExistsException(COUPON_ALREADY_EXISTS);
        }
    }

    // 사용자 쿠폰 사용-미사용 처리
    public UserCoupon useUserCoupon(UserCoupon userCoupon, Boolean couponUse) {
        userCoupon.setCouponUse(couponUse);
        userCoupon.setUseAt(couponUse ? LocalDateTime.now() : null);
        couponRepository.userCouponSave(userCoupon);

        return userCoupon;
    }

    // 사용자 쿠폰 사용 여부 확인
    public void CheckUserCouponIsUsed(UserCoupon userCoupon) {
        if (userCoupon.getCouponUse()) {
            throw new AlreadyExistsException(COUPON_USE_ALREADY_EXISTS);
        }
    }

    // 사용자 쿠폰 조회
    public UserCoupon getUserCouponByUserCouponId(Long userCouponId) {
        return couponRepository.userCouponfindById(userCouponId)
            .orElseThrow(() -> new ResourceNotFoundException(COUPON_NOT_FOUND));
    }

    // 사용자 쿠폰 목록 조회
    public List<UserCoupon> getUserCouponListByUserId(Long userId) {
        return couponRepository.findUserCouponByUserId(userId);
    }

    // 쿠폰 발급 요청
    public boolean couponToQueue(String couponId, String userId) {
        String couponKey = getCouponKey(couponId);
        String issuedCouponKey = getIssuedCouponKey(couponId, userId);
        String queueKey = COUPON_QUEUE_KEY;
        // 유저 발급 확인
        if (redisRepository.isExists(issuedCouponKey)) {
            return false;
        }
        // 쿠폰 수량 확인
        RAtomicLong couponQuantity = redisRepository.getAtomicLong(couponKey);
        if (couponQuantity.get() <= 0) {
            log.warn("쿠폰 수량 부족: key = {}", couponKey);
            return false;
        }

        // 발급하기 위한 큐에 적재
        redisRepository.addScoredSortedSet(queueKey, issuedCouponKey);
        return true;
    }


    // 쿠폰 발급
    public void processCouponQueue() {
        List<UserCoupon> userCouponsList = new java.util.ArrayList<>(List.of());

        // 쿠폰 대기열 조회
        RScoredSortedSet<String> couponQueue = redisRepository.getScoredSortedSet(COUPON_QUEUE_KEY);
        Collection<String> temp = couponQueue.pollFirst(POLL_SIZE);

        // 발급 이력 생성
        RMap<String, Boolean> issuedUsers = redisRepository.getMap(COUPON_ISSUED_KEY);

        for (String key : temp) {
            if (issuedUsers.containsKey(key)) {
                log.warn("이미 발급된 사용자 쿠폰: key = {}", key);
                continue;
            }

            RAtomicLong atomicCoupon = redisRepository.getAtomicLong(
                COUPON_KEY + key.split(":")[1]);

            // 쿠폰 수량 확인하여 재고가 있을 때만 발급
            if (atomicCoupon.get() >= 1) {
                UserCoupon userCoupon = UserCoupon.builder()
                    .userId(Long.parseLong(key.split(":")[3]))
                    .coupon(Coupon.builder().couponId(Long.parseLong(key.split(":")[1])).build())
                    .couponUse(false)
                    .createAt(LocalDateTime.now())
                    .build();
                userCouponsList.add(userCoupon);
                atomicCoupon.decrementAndGet();
                issuedUsers.put(key, true);
            }

        }
        // 쿠폰 발급
        if (!userCouponsList.isEmpty()) {
            couponRepository.userCouponSaveList(userCouponsList);
        }
    }

}
