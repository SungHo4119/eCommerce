package com.hhplush.eCommerce.infrastructure.payment;

import com.hhplush.eCommerce.business.payment.IPaymentRepository;
import com.hhplush.eCommerce.domain.payment.Payment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PaymentRepository implements IPaymentRepository {

    private final IPaymentJPARepository paymentJPARepository;

    public Payment save(Payment payment) {
        return paymentJPARepository.save(payment);
    }
}
