package com.hhplush.eCommerce.domain.exception.custom;

public class ConflictException extends RuntimeException {

    public ConflictException(String message) {
        super(message);
    }
}
