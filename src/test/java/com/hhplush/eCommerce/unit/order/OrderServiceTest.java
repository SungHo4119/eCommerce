package com.hhplush.eCommerce.unit.order;

import static com.hhplush.eCommerce.common.exception.message.ExceptionMessage.ORDER_NOT_FOUND;
import static com.hhplush.eCommerce.common.exception.message.ExceptionMessage.PRODUCT_LIMIT_EXCEEDED;
import static com.hhplush.eCommerce.common.exception.message.ExceptionMessage.PRODUCT_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hhplush.eCommerce.common.exception.custom.LimitExceededException;
import com.hhplush.eCommerce.common.exception.custom.ResourceNotFoundException;
import com.hhplush.eCommerce.domain.order.IOrderRepository;
import com.hhplush.eCommerce.domain.order.Order;
import com.hhplush.eCommerce.domain.order.OrderProduct;
import com.hhplush.eCommerce.domain.order.OrderSerivce;
import com.hhplush.eCommerce.domain.order.OrderState;
import com.hhplush.eCommerce.domain.product.Product;
import com.hhplush.eCommerce.domain.product.ProductQuantity;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class OrderServiceTest {

    @Mock
    private IOrderRepository orderRepository;

    @InjectMocks
    private OrderSerivce orderService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Nested
    @DisplayName("OrderService 의 checkOrderProductList 메서드 테스트")
    class CheckOrderProductListTests {

        @DisplayName("제품 리스트와 주문 리스트가 일치하면 정상적으로 검증한다.")
        @Test
        void checkOrderProductList_success() {
            // given
            List<OrderProduct> orderProductList = new ArrayList<>();
            List<Product> productList = new ArrayList<>();
            for (int i = 0; i < 3; i++) {
                orderProductList.add(OrderProduct.builder().productId((long) i).build());
                productList.add(Product.builder().productId((long) i).build());
            }

            // when / then
            assertDoesNotThrow(
                () -> orderService.checkOrderProductList(orderProductList, productList));
        }

        @DisplayName("제품 리스트와 주문 리스트가 불일치하면 ResourceNotFoundException 예외가 발생해야 한다.")
        @Test
        void PRODUCT_NOT_FOUND_ResourceNotFoundException() {
            // given
            List<OrderProduct> orderProductList = new ArrayList<>();
            List<Product> productList = new ArrayList<>();
            for (int i = 0; i < 2; i++) {
                orderProductList.add(OrderProduct.builder().productId((long) i).build());
            }
            productList.add(Product.builder().productId(1L).build()); // 불일치

            // when
            ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> orderService.checkOrderProductList(orderProductList, productList)
            );

            // then
            assertEquals(PRODUCT_NOT_FOUND, exception.getMessage());
        }
    }

    @Nested
    @DisplayName("OrderService 의 getOrderByOrderId 메서드 테스트")
    class GetOrderByOrderIdTests {

        @DisplayName("존재하는 주문 ID로 주문을 반환한다.")
        @Test
        void getOrderByOrderId_success() {
            // given
            Long orderId = 1L;
            Order order = Order.builder().orderId(orderId).build();
            when(orderRepository.getOrder(orderId)).thenReturn(Optional.of(order));

            // when
            Order result = orderService.getOrderByOrderId(orderId);

            // then
            assertEquals(order, result);
        }

        @DisplayName("존재하지 않는 주문 ID로 ResourceNotFoundException 예외가 발생해야 한다.")
        @Test
        void ORDER_NOT_FOUND_ResourceNotFoundException() {
            // given
            Long orderId = 1L;
            when(orderRepository.getOrder(orderId)).thenReturn(Optional.empty());

            // when
            ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> orderService.getOrderByOrderId(orderId)
            );

            // then
            assertEquals(ORDER_NOT_FOUND, exception.getMessage());
        }
    }

    @Nested
    @DisplayName("OrderService 의 getOrderProductByOrderId 메서드 테스트")
    class GetOrderProductByOrderIdTests {

        @DisplayName("주문 ID로 주문 상품 목록을 반환한다.")
        @Test
        void getOrderProductByOrderId_success() {
            // given
            Long orderId = 1L;
            List<OrderProduct> orderProductList = new ArrayList<>();
            for (int i = 1; i <= 3; i++) {
                orderProductList.add(
                    OrderProduct.builder().productId((long) i).quantity(2L).build());
            }

            when(orderRepository.getOrderProductByOrderId(orderId)).thenReturn(orderProductList);

            // when
            List<OrderProduct> result = orderService.getOrderProductByOrderId(orderId);

            // then
            assertEquals(orderProductList, result);
        }

    }

    @Nested
    @DisplayName("OrderService 의 createOrder 메서드 테스트")
    class CreateOrderTests {

        @DisplayName("올바른 정보로 주문을 생성하면 주문 객체를 반환한다.")
        @Test
        void createOrder_success() {
            // given
            Long userId = 1L;
            Long userCouponId = 10L;
            Long orderAmount = 15000L;
            Long discountAmount = 5000L;

            Order expectedOrder = Order.builder()
                .userId(userId)
                .userCouponId(userCouponId)
                .orderAmount(orderAmount)
                .discountAmount(discountAmount)
                .paymentAmount(10000L) // orderAmount - discountAmount
                .orderState(OrderState.PENDING)
                .orderAt(LocalDateTime.now())
                .build();

            when(orderRepository.orderSave(any(Order.class))).thenReturn(expectedOrder);

            // when
            Order result = orderService.createOrder(userId, userCouponId, orderAmount,
                discountAmount);

            // then
            assertEquals(expectedOrder, result);
            verify(orderRepository).orderSave(any(Order.class));
        }
    }

    @Nested
    @DisplayName("OrderService 의 cancelOrder 메서드 테스트")
    class CancelOrderTests {

        @DisplayName("주문을 취소하면 주문 상태가 FAILED로 변경된다.")
        @Test
        void cancelOrder_success() {
            // given
            Order order = Order.builder().orderState(OrderState.PENDING).build();

            // when
            orderService.cancelOrder(order);

            // then
            assertEquals(OrderState.FAILED, order.getOrderState());
            verify(orderRepository).orderSave(order);
        }
    }

    @Nested
    @DisplayName("OrderService 의 validateProductQuantitie 메서드 테스트")
    class ValidateProductQuantitieTests {

        @DisplayName("주문 가능한 재고가 충분하면 재고를 차감하고 검증에 성공한다.")
        @Test
        void validateProductQuantitie_success() {
            // given
            List<OrderProduct> orderProductList = new ArrayList<>();
            List<ProductQuantity> productQuantityList = new ArrayList<>();
            List<Product> productList = new ArrayList<>();

            for (int i = 0; i < 3; i++) {
                orderProductList.add(
                    OrderProduct.builder().productId((long) i).quantity(5L).build());
                productQuantityList.add(
                    ProductQuantity.builder().productId((long) i).quantity(10L).build());
                productList.add(Product.builder().productId((long) i).build());
            }

            // when
            List<ProductQuantity> result = orderService.validateProductQuantitie(orderProductList,
                productQuantityList, productList);

            // then
            result.forEach(productQuantity -> assertEquals(5L, productQuantity.getQuantity()));
        }

        @DisplayName("주문 수량이 재고보다 많으면 EXCEEDED_LimitExceededException 예외가 발생해야 한다.")
        @Test
        void PRODUCT_LIMIT_EXCEEDED_LimitExceededException() {
            // given
            List<OrderProduct> orderProductList = new ArrayList<>();
            List<ProductQuantity> productQuantityList = new ArrayList<>();

            orderProductList.add(OrderProduct.builder().productId(1L).quantity(15L).build());
            productQuantityList.add(ProductQuantity.builder().productId(1L).quantity(10L).build());

            // when
            LimitExceededException exception = assertThrows(
                LimitExceededException.class,
                () -> orderService.validateProductQuantitie(orderProductList, productQuantityList,
                    new ArrayList<>())
            );

            // then
            assertEquals(PRODUCT_LIMIT_EXCEEDED, exception.getMessage());
        }
    }

    @Nested
    @DisplayName("OrderService 의 calculateOrderAmount 메서드 테스트")
    class CalculateOrderAmountTests {

        @DisplayName("주문 상품 목록과 제품 목록을 기반으로 주문 금액을 올바르게 계산한다.")
        @Test
        void calculateOrderAmount_success() {
            // given
            List<OrderProduct> orderProductList = new ArrayList<>();
            List<Product> productList = new ArrayList<>();

            for (int i = 1; i <= 3; i++) {
                orderProductList.add(
                    OrderProduct.builder().productId((long) i).quantity(2L).build());
                productList.add(Product.builder().productId((long) i).price(1000L * i).build());
            }

            // when
            Long result = orderService.calculateOrderAmount(orderProductList, productList);

            // then
            assertEquals(12000L, result); // 2*1000 + 2*2000 + 2*3000
        }
    }

    @Nested
    @DisplayName("OrderService 의 successOrder 메서드 테스트")
    class SuccessOrderTests {

        @DisplayName("결제 성공 시 주문 상태를 COMPLETED로 변경한다.")
        @Test
        void successOrder_success() {
            // given
            Order order = Order.builder().orderState(OrderState.PENDING).build();

            // when
            orderService.successOrder(order);

            // then
            assertEquals(OrderState.COMPLETED, order.getOrderState());
            verify(orderRepository).orderSave(order);
        }
    }
}
