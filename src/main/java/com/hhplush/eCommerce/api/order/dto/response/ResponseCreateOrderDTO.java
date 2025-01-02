package com.hhplush.eCommerce.api.order.dto.response;

import com.hhplush.eCommerce.domain.enums.OrderState;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record ResponseCreateOrderDTO(
    Integer orderId,
    Integer userId,
    OrderState orderStatus,
    LocalDateTime orderAt
) {

}
