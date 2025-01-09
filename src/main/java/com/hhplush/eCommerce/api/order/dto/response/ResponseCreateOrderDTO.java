package com.hhplush.eCommerce.api.order.dto.response;

import com.hhplush.eCommerce.domain.order.OrderState;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record ResponseCreateOrderDTO(
    Long orderId,
    Long userId,
    OrderState orderStatus,
    LocalDateTime orderAt
) {

}
