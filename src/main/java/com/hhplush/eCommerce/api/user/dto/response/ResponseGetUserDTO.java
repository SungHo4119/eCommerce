package com.hhplush.eCommerce.api.user.dto.response;

import lombok.Builder;

@Builder
public record ResponseGetUserDTO(
    Long userId,
    String userName,
    Long point
) {

}

