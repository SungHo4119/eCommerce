package com.hhplush.eCommerce.infrastructure.coupon;

import com.hhplush.eCommerce.domain.coupon.CouponQuantity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

public interface ICouponQuantityJPARepository extends JpaRepository<CouponQuantity, Long> {

    // 쿠폰 수량 조회
    // 쿠폰 수량을 조회하는데, 조회하는 동안 다른 트랜잭션에서 해당 데이터를 수정하지 못하도록 Lock을 걸어준다.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    CouponQuantity findCouponQuantityByCouponId(Long couponId);

}
