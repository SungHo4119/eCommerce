package com.hhplush.eCommerce.common.exception.custom;

public class AlreadyExistsException extends RuntimeException {

    public AlreadyExistsException(String message) {
        super(message);
    }
}
