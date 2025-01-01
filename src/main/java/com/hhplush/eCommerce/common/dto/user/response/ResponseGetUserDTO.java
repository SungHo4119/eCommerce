package com.hhplush.eCommerce.common.dto.user.response;

import lombok.Builder;

@Builder
public record ResponseGetUserDTO(
    Integer userId,
    String userName,
    Integer point
) {

}

