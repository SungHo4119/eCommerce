package com.hhplush.eCommerce.common.exception;

public record ErrorResponse(
    String code,
    String message
) {

}
