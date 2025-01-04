package com.hhplush.eCommerce.common.exception.custom;

public class LimitExceededException extends RuntimeException {

    public LimitExceededException(String message) {
        super(message);
    }
}
