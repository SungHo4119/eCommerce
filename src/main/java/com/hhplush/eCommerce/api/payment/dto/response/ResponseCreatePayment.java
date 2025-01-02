package com.hhplush.eCommerce.api.payment.dto.response;

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
