package com.hhplush.eCommerce.business.coupon;

import com.hhplush.eCommerce.domain.coupon.Coupon;
import com.hhplush.eCommerce.domain.coupon.CouponQuantity;
import com.hhplush.eCommerce.domain.coupon.CouponService;
import com.hhplush.eCommerce.domain.coupon.UserCoupon;
import com.hhplush.eCommerce.domain.user.User;
import com.hhplush.eCommerce.domain.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CouponUseCase {

    private final UserService userService;
    private final CouponService couponService;

    // 쿠폰 발급
    public UserCoupon issueCoupon(Long couponId, Long userId) {

        User user = userService.getUserByUserId(userId);
        Coupon coupon = couponService.getCouponByCouponId(couponId);
        couponService.checkCouponValidity(userId, couponId);

        // 발급 하기 위한 수량 체크
        CouponQuantity couponQuantity = couponService.checkCouponQuantityWithLock(couponId);

        // 쿠폰 발급
        return couponService.issueUserCoupon(coupon, couponQuantity, user);
    }
}
