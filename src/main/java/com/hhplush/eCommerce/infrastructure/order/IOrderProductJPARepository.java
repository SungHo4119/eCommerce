package com.hhplush.eCommerce.infrastructure.order;

import com.hhplush.eCommerce.domain.order.OrderProduct;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IOrderProductJPARepository extends JpaRepository<OrderProduct, Long> {

    List<OrderProduct> findByOrderId(Long orderId);

}
