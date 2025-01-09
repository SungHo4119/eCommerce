package com.hhplush.eCommerce.business.coupon;

import com.hhplush.eCommerce.business.user.UserLoader;
import com.hhplush.eCommerce.domain.coupon.Coupon;
import com.hhplush.eCommerce.domain.coupon.CouponQuantity;
import com.hhplush.eCommerce.domain.coupon.UserCoupon;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CouponService {

    private final UserLoader userLoader;
    private final CouponLoader couponLoader;
    private final UserCouponLoader userCouponLoader;

    // 쿠폰 발급
    public UserCoupon issueCoupon(Long couponId, Long userId) {
        // 유저 정보 조회
        userLoader.getUserByUserId(userId);

        // 쿠폰 정보 조회
        Coupon coupon = couponLoader.getCouponByCouponId(couponId);

        // 이미 발급 받은 쿠폰인지 체크
        userCouponLoader.checkCouponValidity(userId, couponId);

        // 발급 하기 위한 수량 체크
        CouponQuantity couponQuantity = couponLoader.checkCouponQuantity(couponId);

        // 쿠폰 발급
        UserCoupon userCoupon = userCouponLoader.issueUserCoupon(coupon, userId);

        // 쿠폰 수량 감소
        couponLoader.decreaseCouponQuantity(couponQuantity);

        // 응답
        return userCoupon;
    }

}
