package com.hhplush.eCommerce.infrastructure.order;

import com.hhplush.eCommerce.domain.order.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IOrderJPARepository extends JpaRepository<Order, Long> {

}
