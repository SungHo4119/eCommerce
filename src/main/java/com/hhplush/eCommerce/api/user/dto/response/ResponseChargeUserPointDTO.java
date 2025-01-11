package com.hhplush.eCommerce.api.user.dto.response;

import lombok.Builder;

@Builder
public record ResponseChargeUserPointDTO(
    Long userId,
    String userName,
    Long point
) {

}

