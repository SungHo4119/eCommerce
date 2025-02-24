package com.hhplush.eCommerce.business.payment;

import com.hhplush.eCommerce.business.payment.event.PaymentCompletedEvent;
import com.hhplush.eCommerce.common.exception.custom.InvalidPaymentCancellationException;
import com.hhplush.eCommerce.domain.event.EventService;
import com.hhplush.eCommerce.domain.event.EventType;
import com.hhplush.eCommerce.domain.event.OutboxEvent;
import com.hhplush.eCommerce.domain.event.ProcessState;
import com.hhplush.eCommerce.domain.order.Order;
import com.hhplush.eCommerce.domain.order.OrderProduct;
import com.hhplush.eCommerce.domain.order.OrderSerivce;
import com.hhplush.eCommerce.domain.payment.Payment;
import com.hhplush.eCommerce.domain.payment.PaymentService;
import com.hhplush.eCommerce.domain.product.ProductQuantity;
import com.hhplush.eCommerce.domain.product.ProductService;
import com.hhplush.eCommerce.domain.user.User;
import com.hhplush.eCommerce.domain.user.UserService;
import com.hhplush.eCommerce.infrastructure.redis.DistributedLock;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Service
@Transactional(noRollbackFor = InvalidPaymentCancellationException.class)
@RequiredArgsConstructor
public class PaymentUseCase {

    private final OrderSerivce orderSerivce;
    private final UserService userService;
    private final PaymentService paymentService;
    private final ProductService productService;
    private final ApplicationEventPublisher eventPublisher; // 이벤트 발행기 추가
    private final EventService eventService;      // 이벤트 저장소 추가 ( 카프카 )

    public Payment processPayment(Long orderId) {
        // 주문 정보 조회
        Order order = orderSerivce.getOrderByOrderIdWithLock(orderId);

        orderSerivce.checkOrderStateIsCompleted(order);

        // 유저 정보 조회
        User user = userService.getUserByUserId(order.getUserId());

        // 결재
        try {
            // 잔액 차감
            userService.decreaseUserPoint(user, order.getPaymentAmount());

            // 주문 상태 변경
            orderSerivce.successOrder(order);

            // 결재 생성
            Payment payment = paymentService.createPayment(order);
            // 이벤트 발행
            eventPublisher.publishEvent(new PaymentCompletedEvent(payment));
            return payment;

        } catch (InvalidPaymentCancellationException e) {
            /**
             * 결재 취소 로직 => 이부분도 이벤트 기반으로 동작 하도록 변경 않을까?
             * ToDo: 결재 취소 로직을 이벤트 기반으로 변경
             */
            // 주문 취소
            orderSerivce.cancelOrder(order);

            // 주문 상품 조회
            List<OrderProduct> orderProductList = orderSerivce.getOrderProductByOrderId(orderId);

            // 상품 재고 조회
            List<ProductQuantity> productQuantityList = productService.getProductQuantityListWithLock(
                orderProductList.stream().map(OrderProduct::getProductId).toList());

            // 상품 재고 복구
            productService.cancelProductQuantity(productQuantityList, orderProductList);

            // 결재 취소
            throw e;
        }
    }

    @DistributedLock(key = "orderId")
    public Payment processPaymentWithRedis(Long orderId) {
        // 주문 정보 조회
        Order order = orderSerivce.getOrderByOrderId(orderId);

        orderSerivce.checkOrderStateIsCompleted(order);

        // 유저 정보 조회
        User user = userService.getUserByUserId(order.getUserId());

        // 결재
        try {
            // 잔액 차감
            userService.decreaseUserPoint(user, order.getPaymentAmount());

            // 주문 상태 변경
            orderSerivce.successOrder(order);

            // 결재 생성
            return paymentService.createPayment(order);
        } catch (InvalidPaymentCancellationException e) {
            // 주문 취소
            orderSerivce.cancelOrder(order);

            // 주문 상품 조회
            List<OrderProduct> orderProductList = orderSerivce.getOrderProductByOrderId(orderId);

            // 상품 재고 조회
            List<ProductQuantity> productQuantityList = productService.getProductQuantityListWithLock(
                orderProductList.stream().map(OrderProduct::getProductId).toList());

            // 상품 재고 복구
            productService.cancelProductQuantity(productQuantityList, orderProductList);

            // 결재 취소
            throw e;
        }
    }


    public Payment processPaymentWtihPublisher(Long orderId) {
        // 주문 정보 조회
        Order order = orderSerivce.getOrderByOrderIdWithLock(orderId);

        orderSerivce.checkOrderStateIsCompleted(order);

        // 유저 정보 조회
        User user = userService.getUserByUserId(order.getUserId());

        // 결재
        try {
            // 잔액 차감
            userService.decreaseUserPoint(user, order.getPaymentAmount());

            // 주문 상태 변경
            orderSerivce.successOrder(order);

            // 결재 생성
            Payment payment = paymentService.createPayment(order);
            // 이벤트 발행
            eventService.saveEvent(OutboxEvent.builder()
                .aggregateId(payment.getPaymentId().toString())
                .eventType(EventType.payment_events.toString())
                .eventPayload(payment.toString())
                .processState(ProcessState.PENDING)
                .build());
            return payment;

        } catch (InvalidPaymentCancellationException e) {
            /**
             * 결재 취소 로직 => 이부분도 이벤트 기반으로 동작 하도록 변경 않을까?
             * ToDo: 결재 취소 로직을 이벤트 기반으로 변경
             */
            // 주문 취소
            orderSerivce.cancelOrder(order);

            // 주문 상품 조회
            List<OrderProduct> orderProductList = orderSerivce.getOrderProductByOrderId(orderId);

            // 상품 재고 조회
            List<ProductQuantity> productQuantityList = productService.getProductQuantityListWithLock(
                orderProductList.stream().map(OrderProduct::getProductId).toList());

            // 상품 재고 복구
            productService.cancelProductQuantity(productQuantityList, orderProductList);

            // 결재 취소
            throw e;
        }
    }

    @Transactional
    @Scheduled(fixedDelay = 1000) // 1초마다 실행
    public void publishEvents() {
        List<OutboxEvent> events = eventService.findByProcessState(ProcessState.PENDING);

        for (OutboxEvent event : events) {
            try {
                // 1. Kafka 발행
                eventService.publishEvent(event.getEventType(),
                    event.getOutboxEventId().toString());
                log.info("Event published: {}", event.getOutboxEventId().toString());

                // 2. 성공적으로 발행
                event.setProcessState(ProcessState.PUBLISHED);
                eventService.saveEvent(event);
            } catch (Exception e) {
                // 3. 발행 실패
                event.setProcessState(ProcessState.PUBLISHED_FAILED);
            }
        }
    }


    @Transactional
    @Scheduled(fixedDelay = 1000) // 1초마다 실행
    public void publishEventsPublishedFailed() {
        List<OutboxEvent> events = eventService.findByProcessState(ProcessState.PUBLISHED_FAILED);

        for (OutboxEvent event : events) {
            try {
                // 1. Kafka 발행
                eventService.publishEvent(event.getEventType(),
                    event.getOutboxEventId().toString());
                log.info("PUBLISHED_FAILED Event published: {}",
                    event.getOutboxEventId().toString());

                // 2. 성공적으로 발행
                event.setProcessState(ProcessState.PUBLISHED);
                eventService.saveEvent(event);
            } catch (Exception e) {
                // 3. 발행 실패
                event.setProcessState(ProcessState.FAILED);
            }
        }
    }
}
