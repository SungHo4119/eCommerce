package com.hhplush.eCommerce.common.dto.coupon.response;

import java.time.LocalDateTime;
import lombok.Builder;
import org.springframework.lang.Nullable;

@Builder
public record ResponseIssueCouponDTO(
    Integer userCouponId,
    Integer couponId,
    Integer userId,
    Boolean use,
    @Nullable LocalDateTime useAt,
    LocalDateTime createdAt
) {

}
