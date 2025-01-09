package com.hhplush.eCommerce.business.coupon;

import static com.hhplush.eCommerce.common.exception.message.ExceptionMessage.COUPON_ALREADY_EXISTS;
import static com.hhplush.eCommerce.common.exception.message.ExceptionMessage.COUPON_NOT_FOUND;

import com.hhplush.eCommerce.business.user.IUserRepository;
import com.hhplush.eCommerce.common.exception.custom.AlreadyExistsException;
import com.hhplush.eCommerce.common.exception.custom.ResourceNotFoundException;
import com.hhplush.eCommerce.domain.coupon.Coupon;
import com.hhplush.eCommerce.domain.coupon.UserCoupon;
import com.hhplush.eCommerce.domain.coupon.UserCouponInfo;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserCouponLoader {


    private final IUserRepository userRepository;
    private final ICouponRepository couponRepository;

    // 사용자 쿠폰 목록 조회
    public List<UserCouponInfo> getUserCouponListByUserId(Long userId) {
        return userRepository.findUserCouponByUserId(userId);
    }

    // 사용자 쿠폰 조회
    public UserCoupon getUserCouponByUserCouponId(Long userCouponId) {
        Optional<UserCoupon> userCoupon = couponRepository.userCouponfindById(userCouponId);
        if (userCoupon.isEmpty()) {
            throw new ResourceNotFoundException(COUPON_NOT_FOUND);
        }
        return userCoupon.get();
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

    // 사용자 쿠폰 발급
    public UserCoupon issueUserCoupon(Coupon coupon, Long userId) {
        UserCoupon userCoupon = UserCoupon.builder()
            .coupon(coupon)
            .userId(userId)
            .couponUse(false)
            .useAt(null)
            .createAt(LocalDateTime.now())
            .build();

        couponRepository.userCouponSave(userCoupon);
        return userCoupon;
    }

    // 사용자 쿠폰 사용-미사용 처리
    public UserCoupon useUserCoupon(UserCoupon userCoupon, Boolean couponUse) {
        userCoupon.setCouponUse(couponUse);
        userCoupon.setUseAt(couponUse ? LocalDateTime.now() : null);
        couponRepository.userCouponSave(userCoupon);
        return userCoupon;
    }

}
