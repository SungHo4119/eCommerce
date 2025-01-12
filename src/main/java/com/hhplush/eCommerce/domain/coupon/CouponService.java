package com.hhplush.eCommerce.domain.coupon;

import static com.hhplush.eCommerce.common.exception.message.ExceptionMessage.COUPON_ALREADY_EXISTS;
import static com.hhplush.eCommerce.common.exception.message.ExceptionMessage.COUPON_LIMIT_EXCEEDED;
import static com.hhplush.eCommerce.common.exception.message.ExceptionMessage.COUPON_NOT_FOUND;
import static com.hhplush.eCommerce.common.exception.message.ExceptionMessage.COUPON_USE_ALREADY_EXISTS;

import com.hhplush.eCommerce.common.exception.custom.AlreadyExistsException;
import com.hhplush.eCommerce.common.exception.custom.LimitExceededException;
import com.hhplush.eCommerce.common.exception.custom.ResourceNotFoundException;
import com.hhplush.eCommerce.domain.user.User;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CouponService {

    private final ICouponRepository couponRepository;

    // 쿠폰 정보 조회
    public Coupon getCouponByCouponId(Long couponId) {
        // 쿠폰 정보 체크
        Optional<Coupon> coupon = couponRepository.findById(couponId);
        if (coupon.isEmpty()) {
            throw new ResourceNotFoundException(COUPON_NOT_FOUND);
        }
        return coupon.get();
    }


    // 쿠폰 수량 체크
    public CouponQuantity checkCouponQuantity(Long couponId) {
        CouponQuantity couponQuantity = couponRepository.findCouponQuantityByCouponId(couponId);
        if (couponQuantity.getQuantity() <= 0) {
            throw new LimitExceededException(COUPON_LIMIT_EXCEEDED);
        }
        return couponQuantity;
    }

    // 쿠폰 발급
    public UserCoupon issueUserCoupon(Coupon coupon, CouponQuantity couponQuantity, User user) {
        UserCoupon userCoupon = new UserCoupon(coupon, user.getUserId());
        couponRepository.userCouponSave(userCoupon);

        // 재고 감소
        couponQuantity.decreaseCouponCount();
        couponRepository.couponQuantitySave(couponQuantity);
        return userCoupon;
    }


    // 이미 발급 받은 쿠폰인지 체크
    public void checkCouponValidity(Long userId, Long couponId) {
        Optional<UserCoupon> userCoupon = couponRepository.findByUserIdAndCouponId(userId,
            couponId);

        // 쿠폰이 존재한다면 이미 발급 받은 쿠폰이므로 예외 처리
        if (userCoupon.isPresent()) {
            throw new AlreadyExistsException(COUPON_ALREADY_EXISTS);
        }
    }

    // 사용자 쿠폰 사용-미사용 처리
    public UserCoupon useUserCoupon(UserCoupon userCoupon, Boolean couponUse) {
        userCoupon.setCouponUse(couponUse);
        userCoupon.setUseAt(couponUse ? LocalDateTime.now() : null);
        couponRepository.userCouponSave(userCoupon);

        return userCoupon;
    }

    // 사용자 쿠폰 사용 여부 확인
    public void CheckUserCouponIsUsed(UserCoupon userCoupon) {
        if (userCoupon.getCouponUse()) {
            throw new ResourceNotFoundException(COUPON_USE_ALREADY_EXISTS);
        }
    }

    // 사용자 쿠폰 조회
    public UserCoupon getUserCouponByUserCouponId(Long userCouponId) {
        Optional<UserCoupon> userCoupon = couponRepository.userCouponfindById(userCouponId);
        if (userCoupon.isEmpty()) {
            throw new ResourceNotFoundException(COUPON_NOT_FOUND);
        }
        return userCoupon.get();
    }

    // 사용자 쿠폰 목록 조회
    public List<UserCoupon> getUserCouponListByUserId(Long userId) {
        return couponRepository.findUserCouponByUserId(userId);
    }

}
