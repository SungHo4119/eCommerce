package com.hhplush.eCommerce.infrastructure.coupon;

import com.hhplush.eCommerce.domain.coupon.UserCoupon;
import com.hhplush.eCommerce.domain.coupon.UserCouponInfo;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface IUserCouponJPARepository extends JpaRepository<UserCoupon, Long> {

    Optional<UserCoupon> findByUserIdAndCouponId(Long userId, Long couponId);

    @Query(
        " select "
            + "new com.hhplush.eCommerce.domain.coupon.UserCouponInfo(uc.userCouponId, c, uc.userId, uc.couponUse, uc.useAt, uc.createAt) "
            + " from UserCoupon uc "
            + " join uc.coupon c "
            + " where uc.userId = :userId "
    )
    List<UserCouponInfo> findCouponsByUserId(@Param("userId") Long userId);
}
