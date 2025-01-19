package com.hhplush.eCommerce.api.coupon.dto.response;

import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record ResponseIssueCouponDTO(
    Long userCouponId,
    Long couponId,
    Long userId,
    Boolean couponUse,
    LocalDateTime useAt,
    LocalDateTime createAt
) {

}
