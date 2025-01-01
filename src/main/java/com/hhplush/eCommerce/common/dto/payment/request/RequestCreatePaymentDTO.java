package com.hhplush.eCommerce.common.dto.payment.request;

public record RequestCreatePaymentDTO(
    Integer orderId,
    Integer user_coupon_id
) {

}
