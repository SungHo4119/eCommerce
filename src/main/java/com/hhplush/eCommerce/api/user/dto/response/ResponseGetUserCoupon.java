package com.hhplush.eCommerce.api.user.dto.response;

import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record ResponseGetUserCoupon(
    Integer userCouponId,
    Coupon coupon,
    Integer userId,
    Boolean use,
    LocalDateTime useAt,
    LocalDateTime createdAt
) {

    @Builder
    public record Coupon(
        Integer couponId,
        String couponName,
        Integer discountAmount
    ) {

    }
}

