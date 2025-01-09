package com.hhplush.eCommerce.infrastructure.payment;

import com.hhplush.eCommerce.domain.payment.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IPaymentJPARepository extends JpaRepository<Payment, Long> {

}
