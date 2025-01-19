package com.hhplush.eCommerce.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hhplush.eCommerce.business.payment.PaymentUseCase;
import com.hhplush.eCommerce.common.exception.custom.InvalidPaymentCancellationException;
import com.hhplush.eCommerce.domain.IDataCenter;
import com.hhplush.eCommerce.domain.order.Order;
import com.hhplush.eCommerce.domain.order.OrderProduct;
import com.hhplush.eCommerce.domain.order.OrderSerivce;
import com.hhplush.eCommerce.domain.payment.Payment;
import com.hhplush.eCommerce.domain.payment.PaymentService;
import com.hhplush.eCommerce.domain.product.ProductQuantity;
import com.hhplush.eCommerce.domain.product.ProductService;
import com.hhplush.eCommerce.domain.user.User;
import com.hhplush.eCommerce.domain.user.UserService;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class PaymentUseCaseTest {

    @Mock
    private OrderSerivce orderSerivce;

    @Mock
    private UserService userService;

    @Mock
    private PaymentService paymentService;

    @Mock
    private ProductService productService;

    @Mock
    private IDataCenter dataCenter;

    @InjectMocks
    private PaymentUseCase paymentUseCase;

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

            when(orderSerivce.getOrderByOrderId(orderId)).thenReturn(order);
            when(userService.getUserByUserId(order.getUserId())).thenReturn(user);
            when(paymentService.createPayment(order)).thenReturn(payment);

            // when
            Payment result = paymentUseCase.processPayment(orderId);

            // then
            assertEquals(payment, result);
            verify(userService, times(1)).decreaseUserPoint(user, order.getPaymentAmount());
            verify(paymentService, times(1)).createPayment(order);
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

            when(orderSerivce.getOrderByOrderId(orderId)).thenReturn(order);
            when(userService.getUserByUserId(order.getUserId())).thenReturn(user);
            doThrow(InvalidPaymentCancellationException.class)
                .when(userService).decreaseUserPoint(user, order.getPaymentAmount());
            when(orderSerivce.getOrderProductByOrderId(orderId)).thenReturn(orderProductList);
            when(productService.getProductQuantityListWithLock(
                List.of(101L, 102L))).thenReturn(productQuantityList);

            // when
            InvalidPaymentCancellationException exception = assertThrows(
                InvalidPaymentCancellationException.class,
                () -> paymentUseCase.processPayment(orderId));

            // then
            verify(orderSerivce, times(1)).cancelOrder(order);
            verify(productService, times(1))
                .cancelProductQuantity(productQuantityList, orderProductList);

        }
    }
}