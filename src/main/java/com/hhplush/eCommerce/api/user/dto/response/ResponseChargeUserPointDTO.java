package com.hhplush.eCommerce.api.user.dto.response;

import lombok.Builder;

@Builder
public record ResponseChargeUserPointDTO(
    Integer userId,
    String userName,
    Integer point
) {

}

