package com.hhplush.eCommerce.integration.business.coupon;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.hhplush.eCommerce.domain.coupon.Coupon;
import com.hhplush.eCommerce.domain.coupon.CouponQuantity;
import com.hhplush.eCommerce.domain.coupon.CouponState;
import com.hhplush.eCommerce.domain.coupon.UserCoupon;
import com.hhplush.eCommerce.domain.user.User;
import com.hhplush.eCommerce.integration.config.IntegrationTest;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

public class CouponUseCaseTest extends IntegrationTest {

    @Nested
    @DisplayName("쿠폰 발급")
    @Transactional
    class IssueCoupon {

        @Test
        void 쿠폰발급_성공() {
            // given
            User user = User.builder()
                .userName("Test User")
                .point(100L)
                .build();
            user = userJPARepository.save(user);

            Coupon coupon = Coupon.builder()
                .couponName("Test Coupon")
                .discountAmount(1000L)
                .couponState(CouponState.ISSUABLE)
                .build();

            coupon = couponJPARepository.save(coupon);

            CouponQuantity cq = CouponQuantity.builder()
                .couponId(coupon.getCouponId())
                .quantity(10L)
                .build();

            cq = couponQuantityJPARepository.save(cq);

            // when
            UserCoupon result = couponUseCase.issueCoupon(coupon.getCouponId(), user.getUserId());

            // 쿠폰 발급 후 쿠폰 발급 수량 감소 확인
            CouponQuantity couponQuantity = couponQuantityJPARepository.findById(
                cq.getCouponQuantityId()).get();
            // then
            assertEquals(user.getUserId(), result.getUserId());
            assertEquals(coupon.getCouponId(), result.getCouponId());
            assertEquals(false, result.getCouponUse());
            assertEquals(9L, couponQuantity.getQuantity());
        }

    }

    @Nested
    @DisplayName("쿠폰 발급 동시성 제어")
    class IssueCouponConcurrency {

        @Test
        void 쿠폰발급_동시성_성공_10개() throws InterruptedException {
            // given
            User user = User.builder()
                .userName("Test User")
                .point(100L)
                .build();
            user = userJPARepository.save(user);

            Coupon coupon = Coupon.builder()
                .couponName("Test Coupon")
                .discountAmount(1000L)
                .couponState(CouponState.ISSUABLE)
                .build();

            coupon = couponJPARepository.save(coupon);

            CouponQuantity cq = CouponQuantity.builder()
                .couponId(coupon.getCouponId())
                .quantity(10L)
                .build();

            cq = couponQuantityJPARepository.save(cq);

            // when
            ExecutorService executorService = Executors.newFixedThreadPool(10);
            CountDownLatch countDownLatch = new CountDownLatch(10);

            for (int i = 0; i < 10; i++) {
                Long couponId = coupon.getCouponId();
                Long userId = user.getUserId();
                executorService.execute(() -> {
                    try {
                        couponUseCase.issueCoupon(couponId, userId);
                    } catch (Exception e) {
                    } finally {
                        countDownLatch.countDown();
                    }
                });
            }

            countDownLatch.await();
            executorService.shutdown();
            // then
            cq = couponQuantityJPARepository.findById(cq.getCouponQuantityId()).get();
            List<UserCoupon> userCouponList = userCouponJPARepository.findAll();
            assertEquals(9L, cq.getQuantity());
            assertEquals(1, userCouponList.size());
        }

    }

}
