package com.hhplush.eCommerce.api.order.dto.request;

import static com.hhplush.eCommerce.common.exception.message.ExceptionMessage.INVALID_ID;
import static com.hhplush.eCommerce.common.exception.message.ExceptionMessage.INVALID_QUANTITY;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import java.util.List;

public record RequestCreateOrderDTO(
    @Min(value = 1, message = INVALID_ID)
    @Schema(description = "유저 ID", example = "1")
    Long userId,
    @Min(value = 1, message = INVALID_ID)
    @Schema(description = "유저 쿠폰 ID", example = "1")
    Long userCouponId,
    @Valid
    List<RequestProducts> product
) {

    public record RequestProducts(
        @Min(value = 1, message = INVALID_ID)
        @Schema(description = "제품 ID", example = "1")
        Long productId,
        @Min(value = 1, message = INVALID_QUANTITY)
        @Schema(description = "제품 수량", example = "1")
        Long quantity
    ) {

    }
}
