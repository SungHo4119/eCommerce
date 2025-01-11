package com.hhplush.eCommerce.api.payment.dto.response;

import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record ResponseCreatePayment(
    Long paymentId,
    Long orderId,
    LocalDateTime paymentAt
) {

}
