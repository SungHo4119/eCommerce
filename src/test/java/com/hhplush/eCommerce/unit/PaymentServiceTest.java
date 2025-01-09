package com.hhplush.eCommerce.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hhplush.eCommerce.business.dataCenter.IDataCenter;
import com.hhplush.eCommerce.business.order.OrderLoader;
import com.hhplush.eCommerce.business.payment.PaymentLoader;
import com.hhplush.eCommerce.business.payment.PaymentService;
import com.hhplush.eCommerce.business.product.ProductLoader;
import com.hhplush.eCommerce.business.user.UserLoader;
import com.hhplush.eCommerce.common.exception.custom.InvalidPaymentCancellationException;
import com.hhplush.eCommerce.domain.order.Order;
import com.hhplush.eCommerce.domain.order.OrderProduct;
import com.hhplush.eCommerce.domain.payment.Payment;
import com.hhplush.eCommerce.domain.product.ProductQuantity;
import com.hhplush.eCommerce.domain.user.User;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class PaymentServiceTest {

    @Mock
    private OrderLoader orderLoader;

    @Mock
    private UserLoader userLoader;

    @Mock
    private PaymentLoader paymentLoader;

    @Mock
    private ProductLoader productLoader;

    @Mock
    private IDataCenter dataCenter;

    @InjectMocks
    private PaymentService paymentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Nested
    class ProcessPaymentTests {

        @Test
        void processPayment_성공() {
            // given
            Long orderId = 1L;
            Order order = Order.builder()
                .orderId(orderId)
                .userId(1L)
                .paymentAmount(1000L)
                .build();
            User user = User.builder().userId(1L).point(2000L).build();
            Payment payment = Payment.builder()
                .paymentId(1L)
                .orderId(orderId)
                .paymentAt(LocalDateTime.now())
                .build();

            when(orderLoader.getOrderByOrderId(orderId)).thenReturn(order);
            when(userLoader.getUserByUserId(order.getUserId())).thenReturn(user);
            when(paymentLoader.createPayment(order)).thenReturn(payment);

            // when
            Payment result = paymentService.processPayment(orderId);

            // then
            assertEquals(payment, result);
            verify(userLoader, times(1)).decreaseUserPoint(user, order.getPaymentAmount());
            verify(paymentLoader, times(1)).createPayment(order);
            verify(dataCenter, times(1)).sendDataCenter();
        }

        @Test
        void INSUFFICIENT_BALANCE_InvalidPaymentCancellationException() {
            // given
            Long orderId = 1L;
            Order order = Order.builder()
                .orderId(orderId)
                .userId(1L)
                .paymentAmount(1000L)
                .build();
            User user = User.builder().userId(1L).point(2000L).build();
            List<OrderProduct> orderProductList = List.of(
                OrderProduct.builder().orderProductId(1L).productId(101L).quantity(2L).build(),
                OrderProduct.builder().orderProductId(2L).productId(102L).quantity(1L).build()
            );
            List<ProductQuantity> productQuantityList = List.of(
                ProductQuantity.builder().productId(101L).quantity(10L).build(),
                ProductQuantity.builder().productId(102L).quantity(5L).build()
            );

            when(orderLoader.getOrderByOrderId(orderId)).thenReturn(order);
            when(userLoader.getUserByUserId(order.getUserId())).thenReturn(user);
            doThrow(InvalidPaymentCancellationException.class)
                .when(userLoader).decreaseUserPoint(user, order.getPaymentAmount());
            when(orderLoader.getOrderProductByOrderId(orderId)).thenReturn(orderProductList);
            when(productLoader.getProductQuantityListWithLock(
                List.of(101L, 102L))).thenReturn(productQuantityList);

            // when
            InvalidPaymentCancellationException exception = assertThrows(
                InvalidPaymentCancellationException.class,
                () -> paymentService.processPayment(orderId));

            // then
            verify(orderLoader, times(1)).cancelOrder(order);
            verify(productLoader, times(1))
                .cancelProductQuantity(productQuantityList, orderProductList);

        }
    }
}