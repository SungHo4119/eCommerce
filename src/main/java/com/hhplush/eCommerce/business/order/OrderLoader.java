package com.hhplush.eCommerce.business.order;


import static com.hhplush.eCommerce.common.exception.message.ExceptionMessage.ORDER_NOT_FOUND;

import com.hhplush.eCommerce.common.exception.custom.ResourceNotFoundException;
import com.hhplush.eCommerce.domain.order.Order;
import com.hhplush.eCommerce.domain.order.OrderProduct;
import com.hhplush.eCommerce.domain.order.OrderState;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderLoader {

    private final IOrderRepository orderRepository;

    // 주문 조회
    public Order getOrderByOrderId(Long orderId) {
        Optional<Order> order = orderRepository.getOrder(orderId);
        if (order.isEmpty()) {
            throw new ResourceNotFoundException(ORDER_NOT_FOUND);
        }
        return order.get();

    }

    // 주문 상품 조회
    public List<OrderProduct> getOrderProductByOrderId(Long orderId) {
        return orderRepository.getOrderProductByOrderId(orderId);
    }

    // 주문 생성
    public Order createOrder(Long userId, Long userCouponId, Long orderAmount, Long discountAmount,
        Long paymentAmount) {
        Order order = Order.builder()
            .userId(userId)
            .userCouponId(userCouponId)
            .orderAmount(orderAmount)
            .discountAmount(discountAmount)
            .paymentAmount(paymentAmount)
            .orderState(OrderState.PENDING)
            .orderAt(LocalDateTime.now())
            .build();
        return orderRepository.orderSave(order);
    }

    // 주문 상품 생성
    public void createOrderProduct(Long orderId, List<OrderProduct> orderProductList) {
        for (OrderProduct orderProduct : orderProductList) {
            orderProduct.setOrderId(orderId);
        }
        orderRepository.orderProductSaveAll(orderProductList);
    }

    // 주문 취소 처리
    public void cancelOrder(Order order) {
        order.setOrderState(OrderState.FAILED);
        orderRepository.orderSave(order);
    }

}
