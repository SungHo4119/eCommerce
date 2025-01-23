package com.hhplush.eCommerce.integration.business.payment;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.hhplush.eCommerce.domain.order.Order;
import com.hhplush.eCommerce.domain.order.OrderState;
import com.hhplush.eCommerce.domain.payment.Payment;
import com.hhplush.eCommerce.domain.user.User;
import com.hhplush.eCommerce.integration.config.IntegrationTest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class PaymentUseCaseTest extends IntegrationTest {

    @Nested
    @DisplayName("결제 동시성 제어")
    class PaymentConcurrency {

        @Test
        void 결제를_여러번_시도할경우_단한번의_결재만_이루워져야한다() throws InterruptedException {
            // given
            int threadCount = 10;
            User user = User.builder()
                .userName("test")
                .point(10000L)
                .build();

            user = userJPARepository.save(user);

            Order order = Order.builder().userId(user.getUserId())
                .orderAmount(1000L)
                .discountAmount(0L)
                .paymentAmount(1000L)
                .orderState(OrderState.PENDING)
                .orderAt(LocalDateTime.now())
                .build();

            order = orderJPARepository.save(order);
            // when

            ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
            CountDownLatch countDownLatch = new CountDownLatch(threadCount);

            for (int i = 0; i < threadCount; i++) {
                Long orderId = order.getOrderId();
                executorService.execute(() -> {
                    try {
                        paymentUseCase.processPayment(orderId);
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
            order = orderJPARepository.findById(order.getOrderId()).get();
            assertEquals(OrderState.COMPLETED, order.getOrderState());

            user = userJPARepository.findById(user.getUserId()).get();
            assertEquals(9000L, user.getPoint());

            List<Payment> payment = paymentJPARepository.findAll();
            assertEquals(1, payment.size());
        }
    }

}
