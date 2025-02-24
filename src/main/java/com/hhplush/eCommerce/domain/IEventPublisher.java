package com.hhplush.eCommerce.domain;

public interface IEventPublisher {

    void publishString(String topic, String message);
}
