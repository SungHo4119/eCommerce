package com.hhplush.eCommerce.domain.exception.custom;

public class LimitExceededException extends RuntimeException {

    public LimitExceededException(String message) {
        super(message);
    }
}
