package com.hhplush.eCommerce.common.dto.user.response;

import lombok.Builder;

@Builder
public record ResponseChargeUserPointDTO(
    Integer userId,
    String userName,
    Integer point
) {

}

