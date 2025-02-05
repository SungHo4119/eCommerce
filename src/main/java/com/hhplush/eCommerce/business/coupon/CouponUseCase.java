package com.hhplush.eCommerce.business.coupon;

import com.hhplush.eCommerce.domain.coupon.Coupon;
import com.hhplush.eCommerce.domain.coupon.CouponQuantity;
import com.hhplush.eCommerce.domain.coupon.CouponService;
import com.hhplush.eCommerce.domain.coupon.UserCoupon;
import com.hhplush.eCommerce.domain.user.User;
import com.hhplush.eCommerce.domain.user.UserService;
import com.hhplush.eCommerce.infrastructure.redis.DistributedLock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CouponUseCase {

    private final UserService userService;
    private final CouponService couponService;

    // 쿠폰 발급
    @Transactional
    public UserCoupon issueCoupon(Long couponId, Long userId) {

        User user = userService.getUserByUserId(userId);
        Coupon coupon = couponService.getCouponByCouponId(couponId);
        couponService.checkCouponValidity(userId, couponId);

        // 발급 하기 위한 수량 체크
        CouponQuantity couponQuantity = couponService.checkCouponQuantityWithLock(couponId);

        // 쿠폰 발급
        return couponService.issueUserCoupon(coupon, couponQuantity, user);
    }

    // 쿠폰 발급
    @DistributedLock(key = "couponId")
    @Transactional
    public UserCoupon issueCouponWithRedis(Long couponId, Long userId) {
        User user = userService.getUserByUserId(userId);
        Coupon coupon = couponService.getCouponByCouponId(couponId);
        couponService.checkCouponValidity(userId, couponId);

        // 발급 하기 위한 수량 체크
        CouponQuantity couponQuantity = couponService.checkCouponQuantity(couponId);

        // 쿠폰 발급
        return couponService.issueUserCoupon(coupon, couponQuantity, user);
    }


    // 쿠폰 발급 요청
    @Transactional
    public void issueCouponRequest(Long couponId, Long userId) {

        User user = userService.getUserByUserId(userId);
        Coupon coupon = couponService.getCouponByCouponId(couponId);
        // 발급 된 쿠폰 있는지 체크
        couponService.checkCouponValidity(userId, couponId);

        // 쿠폰 발급 요청 ( 레디스 Queue (set)에 적재 )
        couponService.couponToQueue(couponId.toString(), userId.toString());
    }

    @Scheduled(fixedDelay = 100) // 0.1초마다 실행
    public void processCouponQueue() {
        // 쿠폰 발급 대기열 처리
        couponService.processCouponQueue();
    }
}
