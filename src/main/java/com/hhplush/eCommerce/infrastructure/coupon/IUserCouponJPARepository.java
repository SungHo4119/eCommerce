package com.hhplush.eCommerce.infrastructure.coupon;

import com.hhplush.eCommerce.domain.coupon.UserCoupon;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface IUserCouponJPARepository extends JpaRepository<UserCoupon, Long> {

    @Query("SELECT uc FROM UserCoupon uc WHERE uc.userId = :userId AND uc.coupon.couponId = :couponId")
    Optional<UserCoupon> findByUserIdAndCouponId(@Param("userId") Long userId,
        @Param("couponId") Long couponId);


    List<UserCoupon> findCouponsByUserId(Long userId);
}
