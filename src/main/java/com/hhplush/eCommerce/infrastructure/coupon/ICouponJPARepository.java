package com.hhplush.eCommerce.infrastructure.coupon;

import com.hhplush.eCommerce.domain.coupon.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ICouponJPARepository extends JpaRepository<Coupon, Long> {

}
