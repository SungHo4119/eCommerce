package com.hhplush.eCommerce.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.hhplush.eCommerce.business.order.IOrderRepository;
import com.hhplush.eCommerce.business.payment.IPaymentRepository;
import com.hhplush.eCommerce.business.payment.PaymentLoader;
import com.hhplush.eCommerce.domain.order.Order;
import com.hhplush.eCommerce.domain.payment.Payment;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class PaymentLoaderTest {

    @Mock
    private IPaymentRepository paymentRepository;
    @Mock
    private IOrderRepository orderRepository;

    @InjectMocks
    private PaymentLoader paymentLoader;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Nested
    @DisplayName("PaymentLoader 의 createPayment 메서드 테스트")
    class GetUserTests {

        @Test
        void createPayment_성공() {
            // Given
            Order order = Order.builder().orderId(1L).build();
            Payment payment = Payment.builder().orderId(order.getOrderId())
                .paymentAt(LocalDateTime.now()).build();

            when(paymentRepository.save(payment)).thenReturn(payment);

            // When
            Payment result = paymentLoader.createPayment(order);
            // Then
            assertEquals(result.getOrderId(), payment.getOrderId());
        }
    }
}
