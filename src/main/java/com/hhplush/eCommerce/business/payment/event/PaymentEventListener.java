package com.hhplush.eCommerce.business.payment.event;

import com.hhplush.eCommerce.domain.IDataCenter;
import com.hhplush.eCommerce.domain.payment.Payment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventListener {

    private final IDataCenter dataCenter;

    @Async
    // 트랜잭션 커밋 이후 실행
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handlePaymentCompleted(PaymentCompletedEvent event) {
        Payment payment = event.getPayment();
        log.info("Processing payment event for orderId: {}", payment.getOrderId());

        dataCenter.sendDataCenter(); // 비동기 실행
    }
}


