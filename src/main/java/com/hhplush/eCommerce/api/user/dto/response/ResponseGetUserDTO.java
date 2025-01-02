package com.hhplush.eCommerce.api.user.dto.response;

import lombok.Builder;

@Builder
public record ResponseGetUserDTO(
    Integer userId,
    String userName,
    Integer point
) {

}

