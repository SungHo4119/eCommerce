package com.hhplush.eCommerce.business.payment;

import com.hhplush.eCommerce.domain.payment.Payment;

public interface IPaymentRepository {

    Payment save(Payment payment);

}
