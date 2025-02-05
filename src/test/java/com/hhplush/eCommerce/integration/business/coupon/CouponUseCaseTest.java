package com.hhplush.eCommerce.integration.business.coupon;

import static com.hhplush.eCommerce.common.utils.RedisUtil.COUPON_ISSUED_KEY;
import static com.hhplush.eCommerce.common.utils.RedisUtil.COUPON_KEY;
import static com.hhplush.eCommerce.common.utils.RedisUtil.COUPON_QUEUE_KEY;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.hhplush.eCommerce.domain.coupon.Coupon;
import com.hhplush.eCommerce.domain.coupon.CouponQuantity;
import com.hhplush.eCommerce.domain.coupon.CouponState;
import com.hhplush.eCommerce.domain.coupon.UserCoupon;
import com.hhplush.eCommerce.domain.user.User;
import com.hhplush.eCommerce.integration.config.IntegrationTest;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.redisson.api.RScoredSortedSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CouponUseCaseTest extends IntegrationTest {

    private static final Logger log = LoggerFactory.getLogger(CouponUseCaseTest.class);

    @Nested
    @DisplayName("쿠폰 발급 동시성 제어 - 비관적 락")
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

    @Nested
    @DisplayName("쿠폰 발급 동시성 제어 - Redis 분산락")
    class IssueCouponConcurrencyWithRedis {

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
                        couponUseCase.issueCouponWithRedis(couponId, userId);
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
                        // 분산락 적용
                        couponUseCase.issueCouponWithRedis(couponId, userId);
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


    @Nested
    @DisplayName("쿠폰 발급 동시성 제어 - Redis")
    class IssueCouponConcurrencyWithRedisService {

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

            // 레디스 스토리지에 쿠폰 재고 10개 저장
            redisService.addCouponQuantity("coupon:" + coupon.getCouponId().toString(), 10L);

            // when
            ExecutorService executorService = Executors.newFixedThreadPool(20);
            CountDownLatch countDownLatch = new CountDownLatch(20);

            for (int i = 0; i < 20; i++) {
                Long couponId = coupon.getCouponId();
                Long userId = user.getUserId();
                executorService.execute(() -> {
                    try {
                        couponUseCase.issueCouponRequest(couponId, userId);
                    } catch (Exception e) {
                    } finally {
                        countDownLatch.countDown();
                    }
                });
            }

            countDownLatch.await();
            executorService.shutdown();

            // 스케쥴러 동작을 위한 슬립
            Thread.sleep(1000);

            // then
            // 쿠폰 발급 확인
            List<UserCoupon> userCoupon = userCouponJPARepository.findAll();
            assertEquals(1, userCoupon.size());
            // 큐 0건인지 확인
            RScoredSortedSet<String> value = redisService.getSetValue(COUPON_QUEUE_KEY);
            assertEquals(0, value.size());
            // 쿠폰 재고 9개인지 확인
            assertEquals(9,
                redisService.getCouponQuantity(COUPON_KEY + coupon.getCouponId().toString()));
            // 쿠폰 발급 이력 1건인지 확인
            assertEquals(1, redisService.getCouponIssuedList(COUPON_ISSUED_KEY));
        }

        @Test
        void 여러_유저가_쿠폰발급_요청하는_경우_갯수만큼만_발급한다() throws InterruptedException {
            // given
            List<User> userList = new ArrayList<>();
            List<Coupon> couponList = new ArrayList<>();
            for (int i = 1; i <= 200; i++) {
                User user = User.builder()
                    .userName("Test User" + i)
                    .point(100L)
                    .build();
                userList.add(user);
            }
            userList = userJPARepository.saveAll(userList);

            Coupon coupon = Coupon.builder()
                .couponName("Test Coupon")
                .discountAmount(1000L)
                .couponState(CouponState.ISSUABLE)
                .build();
            coupon = couponJPARepository.save(coupon);

            Coupon coupon2 = Coupon.builder()
                .couponName("Test Coupon2")
                .discountAmount(1000L)
                .couponState(CouponState.ISSUABLE)
                .build();
            coupon2 = couponJPARepository.save(coupon2);

            redisService.addCouponQuantity("coupon:" + coupon.getCouponId().toString(), 100L);
            redisService.addCouponQuantity("coupon:" + coupon2.getCouponId().toString(), 100L);

            couponList.add(coupon);
            couponList.add(coupon2);

            // when
            Random random = new Random();
            ExecutorService executorService = Executors.newFixedThreadPool(15);
            CountDownLatch countDownLatch = new CountDownLatch(500);

            int couponsize = couponList.size();
            int usersize = userList.size();
            for (int i = 0; i < 500; i++) {
                Long couponId = random.nextLong(couponsize) + 1L;
                Long userId = random.nextLong(usersize) + 1L;
                executorService.execute(() -> {
                    try {
                        couponUseCase.issueCouponRequest(couponId, userId);
                    } catch (Exception e) {
                    } finally {
                        countDownLatch.countDown();
                    }
                });

                log.info("couponId : " + couponId + " userId : " + userId);
            }

            countDownLatch.await();
            executorService.shutdown();

            // 스케쥴러 동작을 위한 슬립
            Thread.sleep(10000);

            // then
            // 쿠폰 발급 확인 ( 맞지 않을 수 있음 ( 쿠폰2에 대해 발급 요청이 0건인 경우 ) )
            List<UserCoupon> userCoupon = userCouponJPARepository.findAll();
            assertEquals(200, userCoupon.size());

            // 쿠폰 재고 학인
            Long couponQuantity = redisService.getCouponQuantity(
                COUPON_KEY + coupon.getCouponId().toString());
            assertEquals(0, couponQuantity);

            couponQuantity = redisService.getCouponQuantity(
                COUPON_KEY + coupon2.getCouponId().toString());
            assertEquals(0, couponQuantity);
            // 쿠폰 발급 이력 1건인지 확인
            Integer issuedList = redisService.getCouponIssuedList(COUPON_ISSUED_KEY);
            assertEquals(200, redisService.getCouponIssuedList(COUPON_ISSUED_KEY));
        }
    }
}
