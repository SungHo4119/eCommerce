package com.hhplush.eCommerce.business.payment.event;

import com.hhplush.eCommerce.domain.payment.Payment;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PaymentCompletedEvent {

    private final Payment payment;
}
