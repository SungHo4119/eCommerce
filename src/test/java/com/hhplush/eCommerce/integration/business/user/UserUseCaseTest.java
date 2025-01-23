package com.hhplush.eCommerce.integration.business.user;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.hhplush.eCommerce.domain.user.User;
import com.hhplush.eCommerce.integration.config.IntegrationTest;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class UserUseCaseTest extends IntegrationTest {

    @Nested
    @DisplayName("유저 포인트 충전 동시성 제어 - 비관적 락")
    class UserPointChargeConcurrency {

        @Test
        void 유저_포인트_여러번_충전시_충전포인트에대한_정합성을_보장해야한다() throws InterruptedException {
            // given
            Long point = 100L;
            int threadCount = 10;
            User user = User.builder()
                .userName("Test User")
                .point(0L)
                .build();

            user = userJPARepository.save(user);
            // when
            ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
            CountDownLatch countDownLatch = new CountDownLatch(threadCount);

            for (int i = 0; i < threadCount; i++) {
                Long userId = user.getUserId();
                executorService.execute(() -> {
                    try {
                        userUseCase.chargeUserPoint(userId, point);
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
            user = userJPARepository.findById(user.getUserId()).get();
            assertEquals(point * threadCount, user.getPoint());
        }
    }


    @Nested
    @DisplayName("유저 포인트 충전 동시성 제어 - 레디스 사용")
    class UserPointChargeConcurrencyWithRedis {

        @Test
        void 유저_포인트_여러번_충전시_충전포인트에대한_정합성을_보장해야한다() throws InterruptedException {
            // given
            Long point = 100L;
            int threadCount = 10;
            User user = User.builder()
                .userName("Test User")
                .point(0L)
                .build();

            user = userJPARepository.save(user);
            // when
            ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
            CountDownLatch countDownLatch = new CountDownLatch(threadCount);

            for (int i = 0; i < threadCount; i++) {
                Long userId = user.getUserId();
                executorService.execute(() -> {
                    try {
                        userUseCase.chargeUserPointWithRedis(userId, point);
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
            user = userJPARepository.findById(user.getUserId()).get();
            assertEquals(point * threadCount, user.getPoint());
        }
    }
}
