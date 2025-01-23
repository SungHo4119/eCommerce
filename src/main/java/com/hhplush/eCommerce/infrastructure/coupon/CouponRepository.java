package com.hhplush.eCommerce.infrastructure.coupon;

import com.hhplush.eCommerce.domain.coupon.Coupon;
import com.hhplush.eCommerce.domain.coupon.CouponQuantity;
import com.hhplush.eCommerce.domain.coupon.ICouponRepository;
import com.hhplush.eCommerce.domain.coupon.UserCoupon;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CouponRepository implements ICouponRepository {

    private final ICouponJPARepository couponJPARepository;
    private final IUserCouponJPARepository userCouponJPARepository;
    private final ICouponQuantityJPARepository couponQuantityJPARepository;

    // 쿠폰 조회
    @Override
    public Optional<Coupon> findById(Long couponId) {
        return couponJPARepository.findById(couponId);
    }

    // 사용자가 발급 받은 쿠폰 조회
    @Override
    public Optional<UserCoupon> findByUserIdAndCouponId(Long userId, Long couponId) {
        return userCouponJPARepository.findByUserIdAndCouponId(userId, couponId);
    }

    // 쿠폰 수량 조회
    @Override
    public CouponQuantity findCouponQuantityByCouponIdWithLock(Long couponId) {
        return couponQuantityJPARepository.findCouponQuantityByCouponId(couponId);
    }

    // 쿠폰 수량 저장
    @Override
    public void couponQuantitySave(CouponQuantity couponQuantity) {
        couponQuantityJPARepository.save(couponQuantity);
    }

    // 유저 쿠폰 발급
    @Override
    public void userCouponSave(UserCoupon userCoupon) {
        userCouponJPARepository.save(userCoupon);
    }

    // 유저 쿠폰 조회
    @Override
    public Optional<UserCoupon> userCouponfindById(Long userCouponId) {
        return userCouponJPARepository.findById(userCouponId);
    }


    @Override
    public List<UserCoupon> findUserCouponByUserId(Long userId) {
        return userCouponJPARepository.findCouponsByUserId(userId);
    }
}
