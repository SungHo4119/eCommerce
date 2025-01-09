package com.hhplush.eCommerce.domain.coupon;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UserCouponInfo {

    private Long userCouponId;
    private Coupon coupon;
    private Long userId;
    private Boolean couponUse;
    private LocalDateTime useAt;
    private LocalDateTime createAt;

    public UserCouponInfo(Long userCouponId, Coupon coupon, Long userId, Boolean couponUse,
        LocalDateTime useAt, LocalDateTime createAt) {
        this.userCouponId = userCouponId;
        this.coupon = coupon;
        this.userId = userId;
        this.couponUse = couponUse;
        this.useAt = useAt;

        this.createAt = createAt;
    }
}

