package com.hhplush.eCommerce.api.payment.dto.request;

public record RequestCreatePaymentDTO(
    Integer orderId,
    Integer user_coupon_id
) {

}
