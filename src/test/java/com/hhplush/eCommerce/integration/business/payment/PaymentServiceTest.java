package com.hhplush.eCommerce.integration.business.payment;

import static com.hhplush.eCommerce.common.exception.message.ExceptionMessage.INSUFFICIENT_BALANCE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.hhplush.eCommerce.common.exception.custom.InvalidPaymentCancellationException;
import com.hhplush.eCommerce.domain.coupon.Coupon;
import com.hhplush.eCommerce.domain.coupon.CouponState;
import com.hhplush.eCommerce.domain.coupon.UserCoupon;
import com.hhplush.eCommerce.domain.order.Order;
import com.hhplush.eCommerce.domain.order.OrderProduct;
import com.hhplush.eCommerce.domain.order.OrderState;
import com.hhplush.eCommerce.domain.payment.Payment;
import com.hhplush.eCommerce.domain.product.Product;
import com.hhplush.eCommerce.domain.product.ProductQuantity;
import com.hhplush.eCommerce.domain.product.ProductState;
import com.hhplush.eCommerce.domain.user.User;
import com.hhplush.eCommerce.integration.config.IntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

public class PaymentServiceTest extends IntegrationTest {

    @Nested
    @DisplayName("결재")
    @Transactional
    class ProcessPayment {

        @Test
        void 결재_성공() {
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
                .quantity(0L)
                .build();

            productQuantity = productQuantityJPARepository.save(productQuantity);

            Order order = Order.builder()
                .userId(user.getUserId())
                .userCouponId(userCoupon.getUserCouponId())
                .orderAmount(1000L)
                .discountAmount(900L)
                .paymentAmount(100L)
                .orderState(OrderState.PENDING)
                .build();

            order = orderJPARepository.save(order);
            // when
            Payment payment = paymentService.processPayment(order.getOrderId());
            // then
            assertEquals(payment.getOrderId(), order.getOrderId());

            // 잔액 확인
            user = userJPARepository.findById(user.getUserId()).get();
            assertEquals(user.getPoint(), 0L);

            // 주문 상태변경 확인
            order = orderJPARepository.findById(order.getOrderId()).get();
            assertEquals(order.getOrderState(), OrderState.COMPLETED);
        }

        @Test
        void 잔액부족시_결재_실패_및_주문_취소() {
            // given
            User user = User.builder()
                .userName("Test User")
                .point(50L)
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
                .quantity(0L)
                .build();

            productQuantity = productQuantityJPARepository.save(productQuantity);

            Order order = Order.builder()
                .userId(user.getUserId())
                .userCouponId(userCoupon.getUserCouponId())
                .orderAmount(1000L)
                .discountAmount(900L)
                .paymentAmount(100L)
                .orderState(OrderState.PENDING)
                .build();
            order = orderJPARepository.save(order);

            OrderProduct orderProduct = OrderProduct.builder()
                .orderId(order.getOrderId())
                .productId(product.getProductId())
                .quantity(2L)
                .build();
            orderProduct = orderProductJPARepository.save(orderProduct);

            Long orderId = order.getOrderId();
            // when
            InvalidPaymentCancellationException exception = assertThrows(
                InvalidPaymentCancellationException.class,
                () -> paymentService.processPayment(orderId));
            // then
            //  주문 취소 확인
            order = orderJPARepository.findById(orderId).get();
            assertEquals(order.getOrderState(), OrderState.FAILED);

            // 주문 상품 재고 확인
            productQuantity = productQuantityJPARepository.findById(
                productQuantity.getProductQuantityId()).get();
            assertEquals(productQuantity.getQuantity(), 2L);

            // 포인트 차감안함
            user = userJPARepository.findById(user.getUserId()).get();
            assertEquals(user.getPoint(), 50L);

            // 오류 체크
            assertEquals(exception.getMessage(), INSUFFICIENT_BALANCE);

        }
    }
}
