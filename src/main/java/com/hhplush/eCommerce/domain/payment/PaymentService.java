package com.hhplush.eCommerce.domain.payment;

import com.hhplush.eCommerce.domain.order.Order;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentService {

    private final IPaymentRepository paymentRepository;

    public Payment createPayment(Order order) {

        // 결재 성공
        Payment payment = Payment.builder().orderId(order.getOrderId())
            .paymentAt(LocalDateTime.now()).build();
        paymentRepository.save(payment);

        return payment;
    }

}
