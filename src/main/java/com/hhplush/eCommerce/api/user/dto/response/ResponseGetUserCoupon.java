package com.hhplush.eCommerce.api.user.dto.response;

import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record ResponseGetUserCoupon(
    Long userCouponId,
    Coupon coupon,
    Long userId,
    Boolean couponUse,
    LocalDateTime useAt,
    LocalDateTime createAt
) {

    @Builder
    public record Coupon(
        Long couponId,
        String couponName,
        Long discountAmount
    ) {

    }
}

