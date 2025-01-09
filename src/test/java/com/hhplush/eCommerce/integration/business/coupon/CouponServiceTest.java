package com.hhplush.eCommerce.integration.business.coupon;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.hhplush.eCommerce.domain.coupon.Coupon;
import com.hhplush.eCommerce.domain.coupon.CouponQuantity;
import com.hhplush.eCommerce.domain.coupon.CouponState;
import com.hhplush.eCommerce.domain.coupon.UserCoupon;
import com.hhplush.eCommerce.domain.user.User;
import com.hhplush.eCommerce.integration.config.IntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

public class CouponServiceTest extends IntegrationTest {

    @Nested
    @DisplayName("쿠폰 발급")
    @Transactional
    class GetProducts {

        @Test
        void 쿠폰발급_성공() {
            // given
            User user = User.builder()
                .userName("Test User")
                .point(100L)
                .build();
            user = userJPARepository.save(user);

            Coupon coupon = Coupon.builder()
                .couponName("Test Coupon")
                .discountAmount(1000L)
                .couponState(CouponState.ISSUABLE)
                .build();

            coupon = couponJPARepository.save(coupon);

            CouponQuantity cq = CouponQuantity.builder()
                .couponId(coupon.getCouponId())
                .quantity(10L)
                .build();

            cq = couponQuantityJPARepository.save(cq);

            // when
            UserCoupon result = couponService.issueCoupon(coupon.getCouponId(), user.getUserId());

            // 쿠폰 발급 후 쿠폰 발급 수량 감소 확인
            CouponQuantity couponQuantity = couponQuantityJPARepository.findById(
                cq.getCouponQuantityId()).get();
            // then
            assertEquals(user.getUserId(), result.getUserId());
            assertEquals(coupon.getCouponId(), result.getCoupon().getCouponId());
            assertEquals(false, result.getCouponUse());
            assertEquals(9L, couponQuantity.getQuantity());
        }

    }
}
