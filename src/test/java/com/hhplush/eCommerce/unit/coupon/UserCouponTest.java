package com.hhplush.eCommerce.unit.coupon;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.hhplush.eCommerce.domain.coupon.Coupon;
import com.hhplush.eCommerce.domain.coupon.UserCoupon;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class UserCouponTest {

    private UserCoupon userCoupon;

    @Nested
    @DisplayName("사용자 쿠폰 도메인 객체 테스트")
    class UserCouponDomainTests {

        @Test
        @DisplayName("사용자 쿠폰 객체를 생성한다.")
        void decreaseCouponCount_success() {
            // Given
            Coupon coupon = Coupon.builder().couponId(1L).build();
            Long userId = 1L;

            // When
            UserCoupon userCoupon = new UserCoupon(coupon, userId);
            // Then
            assertEquals(userCoupon.getCoupon(), coupon);
            assertEquals(userCoupon.getUserId(), userId);
            assertEquals(userCoupon.getCouponUse(), false);
            assertNull(userCoupon.getUseAt());

        }
    }
}
