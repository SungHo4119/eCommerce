package com.hhplush.eCommerce.common.dto.payment.response;

import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record ResponseCreatePayment(
    Integer paymentId,
    Integer orderId,
    Integer userCouponId,
    Integer amount,
    LocalDateTime paymentAt
) {

}
