package com.hhplush.eCommerce.unit;

import static com.hhplush.eCommerce.common.exception.message.ExceptionMessage.COUPON_USE_ALREADY_EXISTS;
import static com.hhplush.eCommerce.common.exception.message.ExceptionMessage.PRODUCT_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.hhplush.eCommerce.business.coupon.CouponLoader;
import com.hhplush.eCommerce.business.coupon.UserCouponLoader;
import com.hhplush.eCommerce.business.order.OrderLoader;
import com.hhplush.eCommerce.business.order.OrderService;
import com.hhplush.eCommerce.business.product.ProductLoader;
import com.hhplush.eCommerce.business.user.UserLoader;
import com.hhplush.eCommerce.common.exception.custom.ResourceNotFoundException;
import com.hhplush.eCommerce.domain.coupon.Coupon;
import com.hhplush.eCommerce.domain.coupon.UserCoupon;
import com.hhplush.eCommerce.domain.order.Order;
import com.hhplush.eCommerce.domain.order.OrderProduct;
import com.hhplush.eCommerce.domain.product.Product;
import com.hhplush.eCommerce.domain.product.ProductQuantity;
import com.hhplush.eCommerce.domain.product.ProductState;
import com.hhplush.eCommerce.domain.user.User;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class OrderServiceTest {

    @Mock
    private UserLoader userLoader;
    @Mock
    private CouponLoader couponLoader;
    @Mock
    private UserCouponLoader userCouponLoader;
    @Mock
    private ProductLoader productLoader;
    @Mock
    private OrderLoader orderLoader;

    @InjectMocks
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Nested
    @DisplayName("OrderService 의 createOrder 메서드 테스트")
    class GetCouponByCouponId {

        @Test
        void COUPON_USE_ALREADY_EXISTS_ResourceNotFoundException() {
            Long userId = 1L;
            Long userCouponId = 1L;
            List<OrderProduct> orderProductList = List.of();
            List<Long> productIds = List.of(1L, 2L);

            // Given
            User user = User.builder()
                .userId(userId)
                .userName("Test User")
                .point(100L)
                .build();
            when(userLoader.getUserByUserId(1L)).thenReturn(user);

            UserCoupon userCoupon = UserCoupon.builder()
                .userCouponId(userCouponId)
                .userId(userId)
                .couponId(1L)
                .couponUse(true)
                .build();
            when(userCouponLoader.getUserCouponByUserCouponId(1L)).thenReturn(userCoupon);
            // When
            ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> orderService.createOrder(userId, userCouponId, orderProductList, productIds));
            // Then
            assertEquals(COUPON_USE_ALREADY_EXISTS, exception.getMessage());
        }

        @Test
        void PRODUCT_NOT_FOUND_ResourceNotFoundException() {
            // input 변수
            Long userId = 1L;
            Long userCouponId = 1L;
            List<OrderProduct> orderProductList = List.of(
                OrderProduct.builder().productId(1L).quantity(5L).build(),
                OrderProduct.builder().productId(2L).quantity(5L).build()
            );
            List<Long> productIds = List.of(1L, 2L);

            // Given
            User user = User.builder()
                .userId(userId)
                .userName("Test User")
                .point(100L)
                .build();
            when(userLoader.getUserByUserId(1L)).thenReturn(user);

            UserCoupon userCoupon = UserCoupon.builder()
                .userCouponId(userCouponId)
                .userId(userId)
                .couponId(1L)
                .couponUse(false)
                .build();

            Coupon coupon = Coupon.builder().couponId(1L).couponName("couponName")
                .discountAmount(100L).build();
            // 사용자 쿠폰 조회
            when(userCouponLoader.getUserCouponByUserCouponId(1L)).thenReturn(userCoupon);
            // 쿠폰 정보 조회 ( 할인 금액  확인 )
            when(couponLoader.getCouponByCouponId(userCoupon.getCouponId())).thenReturn(coupon);
            // 제품 조회
            List<Product> productList = List.of(
                Product.builder().productId(1L).price(100L).productState(ProductState.IN_STOCK)
                    .build()
            );
            when(productLoader.checkGetProductList(List.of(1L, 2L))).thenReturn(productList);

            // When
            ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> orderService.createOrder(userId, userCouponId, orderProductList, productIds));
            // Then
            assertEquals(PRODUCT_NOT_FOUND, exception.getMessage());
        }

        @Test
        void createOrder_성공() {
            // input 변수
            Long userId = 1L;
            Long userCouponId = 1L;
            List<OrderProduct> orderProductList = List.of(
                OrderProduct.builder().productId(1L).quantity(5L).build(),
                OrderProduct.builder().productId(2L).quantity(5L).build()
            );
            List<Long> productIds = List.of(1L, 2L);

            // Given
            User user = User.builder()
                .userId(userId)
                .userName("Test User")
                .point(100L)
                .build();
            when(userLoader.getUserByUserId(1L)).thenReturn(user);

            UserCoupon userCoupon = UserCoupon.builder()
                .userCouponId(userCouponId)
                .userId(userId)
                .couponId(1L)
                .couponUse(false)
                .build();

            Coupon coupon = Coupon.builder().couponId(1L).couponName("couponName")
                .discountAmount(100L).build();
            // 사용자 쿠폰 조회
            when(userCouponLoader.getUserCouponByUserCouponId(1L)).thenReturn(userCoupon);
            // 쿠폰 정보 조회 ( 할인 금액  확인 )
            when(couponLoader.getCouponByCouponId(userCoupon.getCouponId())).thenReturn(coupon);
            // 제품 조회
            List<Product> productList = List.of(
                Product.builder().productId(1L).price(100L).productState(ProductState.IN_STOCK)
                    .build(),
                Product.builder().productId(2L).price(100L).productState(ProductState.OUT_OF_STOCK)
                    .build()
            );
            when(productLoader.checkGetProductList(List.of(1L, 2L))).thenReturn(productList);

            // 재고 확인
            List<ProductQuantity> productQuantityList = List.of(
                ProductQuantity.builder().productId(1L).quantity(5L).build(),
                ProductQuantity.builder().productId(2L).quantity(5L).build()
            );
            when(productLoader.getProductQuantityListWithLock(List.of(1L, 2L)))
                .thenReturn(productQuantityList);

            // 재고 검증 및 차감

            // 주문 생성
            Order order = Order.builder().orderId(1L).userId(userId).userCouponId(userCouponId)
                .orderAmount(1000L).discountAmount(100L).paymentAmount(900L).build();
            when(orderLoader.createOrder(userId, userCouponId, 1000L, 100L, 900L)).thenReturn(
                order);

            // 재고 업데이트 / 상품 상태 업데이트
            // 쿠폰 사용 처리
            // 주문 상품 저장

            // When
            Order result = orderService.createOrder(userId, userCouponId, orderProductList,
                productIds);
            // Then
            assertEquals(order, result);
        }
    }
}
