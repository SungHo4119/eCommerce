package com.hhplush.eCommerce.domain.order;

import java.util.List;
import java.util.Optional;

public interface IOrderRepository {

    Order orderSave(Order order);

    Optional<Order> getOrder(Long orderId);

    Optional<Order> getOrderWithLock(Long orderId);

    void orderProductSaveAll(List<OrderProduct> orderProductList);

    List<OrderProduct> getOrderProductByOrderId(Long orderId);

}
