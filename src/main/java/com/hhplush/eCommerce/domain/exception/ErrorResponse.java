package com.hhplush.eCommerce.domain.exception;

public record ErrorResponse(
    String code,
    String message
) {
}
