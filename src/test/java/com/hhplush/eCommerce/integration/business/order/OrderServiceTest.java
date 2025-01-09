package com.hhplush.eCommerce.integration.business.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.hhplush.eCommerce.domain.coupon.Coupon;
import com.hhplush.eCommerce.domain.coupon.CouponState;
import com.hhplush.eCommerce.domain.coupon.UserCoupon;
import com.hhplush.eCommerce.domain.order.Order;
import com.hhplush.eCommerce.domain.order.OrderProduct;
import com.hhplush.eCommerce.domain.order.OrderState;
import com.hhplush.eCommerce.domain.product.Product;
import com.hhplush.eCommerce.domain.product.ProductQuantity;
import com.hhplush.eCommerce.domain.product.ProductState;
import com.hhplush.eCommerce.domain.user.User;
import com.hhplush.eCommerce.integration.config.IntegrationTest;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

public class OrderServiceTest extends IntegrationTest {

    @Nested
    @DisplayName("제품 주문")
    @Transactional
    class CreateOrder {

        @Test
        void 주문_성공() {
            // given
            User user = User.builder()
                .userName("Test User")
                .point(100L)
                .build();
            user = userJPARepository.save(user);

            Coupon coupon = Coupon.builder()
                .couponName("Test Coupon")
                .discountAmount(900L)
                .couponState(CouponState.ISSUABLE)
                .build();
            coupon = couponJPARepository.save(coupon);

            UserCoupon userCoupon = UserCoupon.builder()
                .userId(user.getUserId())
                .couponId(coupon.getCouponId())
                .couponUse(false)
                .build();

            userCoupon = userCouponJPARepository.save(userCoupon);

            Product product = Product.builder()
                .productName("Test Product")
                .price(500L)
                .productState(ProductState.IN_STOCK)
                .build();
            product = productJPARepository.save(product);

            ProductQuantity productQuantity = ProductQuantity.builder()
                .productId(product.getProductId())
                .quantity(2L)
                .build();
            productQuantity = productQuantityJPARepository.save(productQuantity);

            List<OrderProduct> orderProduct = List.of(
                OrderProduct.builder().productId(product.getProductId()).quantity(2L).build()
            );

            List<Long> productIds = List.of(product.getProductId());
            // when
            Order order = orderService.createOrder(user.getUserId(), userCoupon.getUserCouponId(),
                orderProduct, productIds);
            // then
            assertThat(order).isNotNull();
            assertEquals(order.getUserId(), user.getUserId());
            assertEquals(order.getOrderAmount(), 1000L);
            assertEquals(order.getDiscountAmount(), 900L);
            assertEquals(order.getPaymentAmount(), 100);
            assertEquals(order.getOrderState(), OrderState.PENDING);

            // 재고 차감 확인
            productQuantity = productQuantityJPARepository.findById(
                productQuantity.getProductQuantityId()).get();
            assertEquals(productQuantity.getQuantity(), 0L);

            // 주문 상품 확인
            List<OrderProduct> orderProducts = orderProductJPARepository.findByOrderId(
                order.getOrderId());
            assertThat(orderProducts).isNotEmpty();
            assertThat(orderProducts).hasSize(1);
        }
    }
}
