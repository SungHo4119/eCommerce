package com.hhplush.eCommerce.business.order;

import com.hhplush.eCommerce.domain.order.Order;
import com.hhplush.eCommerce.domain.order.OrderProduct;
import java.util.List;
import java.util.Optional;

public interface IOrderRepository {

    Order orderSave(Order order);

    Optional<Order> getOrder(Long orderId);

    void orderProductSaveAll(List<OrderProduct> orderProductList);

    List<OrderProduct> getOrderProductByOrderId(Long orderId);

}
