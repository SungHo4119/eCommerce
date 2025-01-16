package com.hhplush.eCommerce.integration.business.order;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.hhplush.eCommerce.domain.coupon.Coupon;
import com.hhplush.eCommerce.domain.coupon.CouponState;
import com.hhplush.eCommerce.domain.coupon.UserCoupon;
import com.hhplush.eCommerce.domain.order.Order;
import com.hhplush.eCommerce.domain.order.OrderProduct;
import com.hhplush.eCommerce.domain.product.Product;
import com.hhplush.eCommerce.domain.product.ProductQuantity;
import com.hhplush.eCommerce.domain.product.ProductState;
import com.hhplush.eCommerce.domain.user.User;
import com.hhplush.eCommerce.integration.config.IntegrationTest;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class OrderUseCaseTest extends IntegrationTest {

    @Nested
    @DisplayName("주문 생성 동시성 제어")
    class OrderCreateConcurrency {

        @Test
        void 주문_생성시_재고에_맞게_주문이_생성된다() throws InterruptedException {
            // given
            User user = User.builder()
                .userName("Test User")
                .point(100L)
                .build();
            user = userJPARepository.save(user);

            List<UserCoupon> userCouponList = new ArrayList<>();
            for (int i = 0; i < 15; i++) {
                Coupon coupon = Coupon.builder()
                    .couponName("Test Coupon")
                    .discountAmount(1000L)
                    .couponState(CouponState.ISSUABLE)
                    .build();

                coupon = couponJPARepository.save(coupon);

                UserCoupon userCoupon = UserCoupon.builder()
                    .coupon(coupon)
                    .userId(user.getUserId())
                    .couponUse(false)
                    .build();
                userCouponList.add(userCouponJPARepository.save(userCoupon));
            }

            Product product = Product.builder()
                .productName("Test Product")
                .price(1000L)
                .productState(ProductState.IN_STOCK)
                .build();

            product = productJPARepository.save(product);

            ProductQuantity productQuantity = ProductQuantity.builder()
                .productId(product.getProductId())
                .quantity(10L)
                .build();

            productQuantity = productQuantityJPARepository.save(productQuantity);

            OrderProduct orderProduct = OrderProduct.builder()
                .productId(product.getProductId())
                .quantity(1L)
                .build();
            // when
            ExecutorService executorService = Executors.newFixedThreadPool(15);
            CountDownLatch countDownLatch = new CountDownLatch(15);

            for (int i = 0; i < 15; i++) {
                Long userCouponId = userCouponList.get(i).getUserCouponId();
                Long userId = user.getUserId();
                List<OrderProduct> orderProductList = List.of(orderProduct);
                executorService.execute(() -> {
                    try {
                        orderUseCase.createOrder(userId, userCouponId, orderProductList);
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    } finally {
                        countDownLatch.countDown();
                    }
                });
            }
            countDownLatch.await();
            executorService.shutdown();
            // then
            productQuantity = productQuantityJPARepository.findById(
                productQuantity.getProductQuantityId()).get();
            List<Order> order = orderJPARepository.findAll();
            product = productJPARepository.findById(product.getProductId()).get();
            assertEquals(0L, productQuantity.getQuantity());
            assertEquals(10, order.size());
            assertEquals(ProductState.OUT_OF_STOCK, product.getProductState());

        }
    }
}
