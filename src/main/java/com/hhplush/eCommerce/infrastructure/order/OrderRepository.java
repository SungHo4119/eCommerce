package com.hhplush.eCommerce.infrastructure.order;

import com.hhplush.eCommerce.business.order.IOrderRepository;
import com.hhplush.eCommerce.domain.order.Order;
import com.hhplush.eCommerce.domain.order.OrderProduct;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;


@Repository
@RequiredArgsConstructor
public class OrderRepository implements IOrderRepository {

    private final IOrderJPARepository orderJPARepository;
    private final IOrderProductJPARepository orderProductJPARepository;


    public Optional<Order> getOrder(Long orderId) {
        // 주문 조회
        return orderJPARepository.findById(orderId);
    }

    public Order orderSave(Order order) {
        // 주문 저장
        return orderJPARepository.save(order);
    }

    public void orderProductSaveAll(List<OrderProduct> orderProductList) {
        // 주문 상품 저장
        orderProductJPARepository.saveAll(orderProductList);
    }

    // 주문 상품 조회
    public List<OrderProduct> getOrderProductByOrderId(Long orderId) {
        return orderProductJPARepository.findByOrderId(orderId);
    }
}
