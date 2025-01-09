package com.hhplush.eCommerce.business.payment;

import com.hhplush.eCommerce.business.order.IOrderRepository;
import com.hhplush.eCommerce.domain.order.Order;
import com.hhplush.eCommerce.domain.order.OrderState;
import com.hhplush.eCommerce.domain.payment.Payment;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentLoader {

    private final IPaymentRepository paymentRepository;
    private final IOrderRepository orderRepository;

    public Payment createPayment(Order order) {
        // 주문 상태 변경
        order.setOrderState(OrderState.COMPLETED);
        orderRepository.orderSave(order);

        // 결재 성공
        Payment payment = Payment.builder().orderId(order.getOrderId())
            .paymentAt(LocalDateTime.now()).build();
        paymentRepository.save(payment);
        return payment;
    }

}
