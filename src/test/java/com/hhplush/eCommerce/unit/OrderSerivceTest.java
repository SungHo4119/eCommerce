package com.hhplush.eCommerce.unit;

import static com.hhplush.eCommerce.common.exception.message.ExceptionMessage.ORDER_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hhplush.eCommerce.domain.order.IOrderRepository;
import com.hhplush.eCommerce.domain.order.OrderSerivce;
import com.hhplush.eCommerce.common.exception.custom.ResourceNotFoundException;
import com.hhplush.eCommerce.domain.order.Order;
import com.hhplush.eCommerce.domain.order.OrderProduct;
import com.hhplush.eCommerce.domain.order.OrderState;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class OrderSerivceTest {

    @Mock
    private IOrderRepository orderRepository;

    @InjectMocks
    private OrderSerivce orderSerivce;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Nested
    @DisplayName("OrderLoader 의 getOrderByOrderId 메서드 테스트")
    class GetOrderByOrderIdTests {

        @Test
        void ORDER_NOT_FOUND_ResourceNotFoundException() {
            // given
            Long orderId = 1L;
            when(orderRepository.getOrder(orderId)).thenReturn(Optional.empty());

            // when
            ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> orderSerivce.getOrderByOrderId(orderId));

            // then
            assertEquals(ORDER_NOT_FOUND, exception.getMessage());
        }

        @Test
        void getOrderByOrderId_성공() {
            // given
            Long orderId = 1L;
            Order order = Order.builder()
                .orderId(orderId)
                .userId(2L)
                .orderAmount(1000L)
                .discountAmount(100L)
                .paymentAmount(900L)
                .orderState(OrderState.PENDING)
                .orderAt(LocalDateTime.now())
                .build();
            when(orderRepository.getOrder(orderId)).thenReturn(Optional.of(order));

            // when
            Order result = orderSerivce.getOrderByOrderId(orderId);

            // then
            assertEquals(order, result);
        }
    }

    @Nested
    @DisplayName("OrderLoader 의 getOrderProductByOrderId 메서드 테스트")
    class GetOrderProductByOrderIdTests {

        @Test
        void getOrderProductByOrderId_성공() {
            // given
            Long orderId = 1L;
            List<OrderProduct> orderProducts = List.of(
                OrderProduct.builder().orderProductId(1L).orderId(orderId).productId(101L)
                    .quantity(2L).build(),
                OrderProduct.builder().orderProductId(2L).orderId(orderId).productId(102L)
                    .quantity(1L).build()
            );
            when(orderRepository.getOrderProductByOrderId(orderId)).thenReturn(orderProducts);

            // when
            List<OrderProduct> result = orderSerivce.getOrderProductByOrderId(orderId);

            // then
            assertEquals(orderProducts, result);
        }
    }

    @Nested
    @DisplayName("OrderLoader 의 createOrder 메서드 테스트")
    class CreateOrderTests {

        @Test
        void createOrder_성공() {
            // given
            Order order = Order.builder()
                .userId(2L)
                .userCouponId(1L)
                .orderAmount(1000L)
                .discountAmount(100L)
                .paymentAmount(900L)
                .orderState(OrderState.PENDING)
                .orderAt(LocalDateTime.now())
                .build();
            when(orderRepository.orderSave(any(Order.class))).thenReturn(order);

            // when
            Order result = orderSerivce.createOrder(2L, 1L, 1000L, 100L, 900L);

            // then
            assertEquals(order, result);
        }
    }

    @Nested
    @DisplayName("OrderLoader 의 createOrderProduct 메서드 테스트")
    class CreateOrderProductTests {

        @Test
        void createOrderProduct_성공() {
            // given
            Long orderId = 1L;
            List<OrderProduct> orderProducts = List.of(
                OrderProduct.builder().orderProductId(1L).productId(101L).quantity(2L).build(),
                OrderProduct.builder().orderProductId(2L).productId(102L).quantity(1L).build()
            );

            // when
            orderSerivce.createOrderProduct(orderId, orderProducts);

            // then
            verify(orderRepository, times(1)).orderProductSaveAll(orderProducts);
        }
    }

    @Nested
    @DisplayName("OrderLoader 의 cancelOrder 메서드 테스트")
    class CancelOrderTests {

        @Test
        void cancelOrder_성공() {
            // given
            Order order = Order.builder()
                .orderId(1L)
                .orderState(OrderState.PENDING)
                .build();

            // when
            orderSerivce.cancelOrder(order);

            // then
            assertEquals(OrderState.FAILED, order.getOrderState());
            verify(orderRepository, times(1)).orderSave(order);
        }
    }
}
