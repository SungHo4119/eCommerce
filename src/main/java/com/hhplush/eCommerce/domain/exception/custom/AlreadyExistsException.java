package com.hhplush.eCommerce.domain.exception.custom;

public class AlreadyExistsException extends RuntimeException {

    public AlreadyExistsException(String message) {
        super(message);
    }
}
