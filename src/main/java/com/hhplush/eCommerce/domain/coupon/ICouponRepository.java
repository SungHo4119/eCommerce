package com.hhplush.eCommerce.domain.coupon;

import java.util.List;
import java.util.Optional;

public interface ICouponRepository {

    // 쿠폰 조회
    Optional<Coupon> findById(Long couponId);

    // 사용자가 발급 받은 쿠폰 조회
    Optional<UserCoupon> findByUserIdAndCouponId(Long userId, Long couponId);

    // 쿠폰 수량 조회
    CouponQuantity findCouponQuantityByCouponIdWithLock(Long couponId);

    CouponQuantity findCouponQuantityByCouponId(Long couponId);

    // 쿠폰 수량 저장
    void couponQuantitySave(CouponQuantity couponQuantity);

    // 유저 쿠폰 발급
    void userCouponSave(UserCoupon userCoupon);

    // 유저 쿠폰 조회
    Optional<UserCoupon> userCouponfindById(Long userCouponId);

    // 사용자 쿠폰 목록 조회
    List<UserCoupon> findUserCouponByUserId(Long userId);


}
