package com.hhplush.eCommerce.business.coupon;

import static com.hhplush.eCommerce.common.exception.message.ExceptionMessage.COUPON_LIMIT_EXCEEDED;
import static com.hhplush.eCommerce.common.exception.message.ExceptionMessage.COUPON_NOT_FOUND;

import com.hhplush.eCommerce.common.exception.custom.LimitExceededException;
import com.hhplush.eCommerce.common.exception.custom.ResourceNotFoundException;
import com.hhplush.eCommerce.domain.coupon.Coupon;
import com.hhplush.eCommerce.domain.coupon.CouponQuantity;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CouponLoader {

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

    // 쿠폰 감소
    public void decreaseCouponQuantity(CouponQuantity couponQuantity) {
        couponQuantity.decreaseCouponCount();
        couponRepository.couponQuantitySave(couponQuantity);
    }

}
