package com.hhplush.eCommerce.api.user.dto.request;

import static com.hhplush.eCommerce.common.exception.message.ExceptionMessage.INVALID_POINT;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;

public record RequestChargeUserPointDTO(
    @Min(value = 1, message = INVALID_POINT)
    @Schema(description = "충전 Point", example = "100")
    Long point
) {

}
