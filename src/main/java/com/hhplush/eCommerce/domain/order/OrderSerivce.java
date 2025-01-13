package com.hhplush.eCommerce.domain.order;


import static com.hhplush.eCommerce.common.exception.message.ExceptionMessage.ORDER_NOT_FOUND;
import static com.hhplush.eCommerce.common.exception.message.ExceptionMessage.PRODUCT_LIMIT_EXCEEDED;
import static com.hhplush.eCommerce.common.exception.message.ExceptionMessage.PRODUCT_NOT_FOUND;

import com.hhplush.eCommerce.common.exception.custom.LimitExceededException;
import com.hhplush.eCommerce.common.exception.custom.ResourceNotFoundException;
import com.hhplush.eCommerce.domain.product.Product;
import com.hhplush.eCommerce.domain.product.ProductQuantity;
import com.hhplush.eCommerce.domain.product.ProductState;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderSerivce {

    private final IOrderRepository orderRepository;

    // 주문 제품 항목 체크
    public void checkOrderProductList(List<OrderProduct> orderProductList,
        List<Product> productList) {
        // 제품 갯수 비교
        if (productList.size() != orderProductList.size()) {
            throw new ResourceNotFoundException(PRODUCT_NOT_FOUND);
        }
    }

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
    public Order createOrder(Long userId, Long userCouponId, Long orderAmount,
        Long discountAmount) {
        Long paymentAmount = (orderAmount - discountAmount) > 0 ? orderAmount - discountAmount : 0;
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


    /**
     * @param orderProductList    - 주문한 재품
     * @param productQuantityList - 제품 재고
     * @param productList         - 제품 목록
     * @return productQuantityList - 재고 차감 후 재고 목록
     */
    public List<ProductQuantity> validateProductQuantitie(List<OrderProduct> orderProductList,
        List<ProductQuantity> productQuantityList, List<Product> productList) {
        // 주문 가능한지 수량 확인
        for (ProductQuantity productQuantity : productQuantityList) {
            OrderProduct orderProduct = orderProductList.stream()
                .filter(op -> op.getProductId().equals(productQuantity.getProductId()))
                .findFirst().orElseThrow(() -> new ResourceNotFoundException(PRODUCT_NOT_FOUND));

            // 재고 수 확인
            if (orderProduct.getQuantity() > productQuantity.getQuantity()) {
                throw new LimitExceededException(PRODUCT_LIMIT_EXCEEDED);
            }

            // 재고 차감
            productQuantity.decreaseProductCount(orderProduct.getQuantity());

            if (productQuantity.getQuantity() == 0) {
                productList.stream()
                    .filter(
                        product -> product.getProductId().equals(productQuantity.getProductId()))
                    .findFirst()
                    .ifPresent(product -> product.setProductState(ProductState.OUT_OF_STOCK));
            }
        }
        return productQuantityList;
    }

    // 주문 금액 계산
    public Long calculateOrderAmount(List<OrderProduct> orderProductList,
        List<Product> productList) {
        return productList.stream()
            .map(product -> product.getPrice() * orderProductList.stream()
                .filter(orderProduct -> orderProduct.getProductId().equals(product.getProductId()))
                .findFirst().get().getQuantity())
            .reduce(0L, Long::sum);
    }

    // 결재 성공하여 주문 상태 변경
    public void successOrder(Order order) {
        order.setOrderState(OrderState.COMPLETED);
        orderRepository.orderSave(order);
    }
}
