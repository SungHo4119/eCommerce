package com.hhplush.eCommerce.api.coupon.dto.response;

import java.time.LocalDateTime;
import lombok.Builder;
import org.springframework.lang.Nullable;

@Builder
public record ResponseIssueCouponDTO(
    Long userCouponId,
    Long couponId,
    Long userId,
    Boolean couponUse,
    @Nullable LocalDateTime useAt,
    LocalDateTime createAt
) {

}
