package com.hhplush.eCommerce.common.dto.order.response;

import com.hhplush.eCommerce.domain.entitiy.OrderState;
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
