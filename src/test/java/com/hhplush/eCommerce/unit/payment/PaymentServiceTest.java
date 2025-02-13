package com.hhplush.eCommerce.unit.payment;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.hhplush.eCommerce.domain.IDataCenter;
import com.hhplush.eCommerce.domain.order.Order;
import com.hhplush.eCommerce.domain.payment.IPaymentRepository;
import com.hhplush.eCommerce.domain.payment.Payment;
import com.hhplush.eCommerce.domain.payment.PaymentService;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class PaymentServiceTest {

    @Mock
    private IPaymentRepository paymentRepository;

    @Mock
    private IDataCenter dataCenter;

    @InjectMocks
    private PaymentService paymentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Nested
    @DisplayName("PaymentService 의 createPayment 메서드 테스트")
    class CreatePaymentTests {

        @DisplayName("결재 성공시 결재 정보를 저장한다.")
        @Test
        void createPayment_success() {
            // given
            Order order = Order.builder().orderId(1L).build();
            LocalDateTime now = LocalDateTime.now();
            // when

            Payment payment = paymentService.createPayment(order);
            // then
            assertEquals(payment.getOrderId(), order.getOrderId());
        }

    }
}
