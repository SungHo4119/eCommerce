package com.hhplush.eCommerce.integration.business.coupon;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.hhplush.eCommerce.domain.coupon.Coupon;
import com.hhplush.eCommerce.domain.coupon.CouponQuantity;
import com.hhplush.eCommerce.domain.coupon.CouponState;
import com.hhplush.eCommerce.domain.coupon.UserCoupon;
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

public class CouponUseCaseTest extends IntegrationTest {

    @Nested
    @DisplayName("쿠폰 발급 동시성 제어")
    class IssueCouponConcurrency {

        @Test
        void 동일한_유저가_쿠폰발급_동시에_여러건을_시도할때_1개만_발급한다() throws InterruptedException {
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

        @Test
        void 여러_유저가_쿠폰발급_요청하는_경우_갯수만큼만_발급한다() throws InterruptedException {
            // given
            List<User> userList = new ArrayList<>();
            for (int i = 1; i <= 15; i++) {
                User user = User.builder()
                    .userName("Test User" + i)
                    .point(100L)
                    .build();
                userList.add(userJPARepository.save(user));
            }

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
            ExecutorService executorService = Executors.newFixedThreadPool(15);
            CountDownLatch countDownLatch = new CountDownLatch(15);

            for (int i = 0; i < 15; i++) {
                Long couponId = coupon.getCouponId();
                Long userId = userList.get(i).getUserId();
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
            assertEquals(0L, cq.getQuantity());
            assertEquals(10, userCouponList.size());
        }
    }

}