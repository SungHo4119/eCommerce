package com.hhplush.eCommerce.api.payment.dto.request;

import static com.hhplush.eCommerce.common.exception.message.ExceptionMessage.INVALID_ID;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;

public record RequestCreatePaymentDTO(

    @Min(value = 1, message = INVALID_ID)
    @Schema(description = "주문 ID", example = "1")
    Long orderId
) {

}
