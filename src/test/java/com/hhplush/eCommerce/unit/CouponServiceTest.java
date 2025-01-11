package com.hhplush.eCommerce.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.hhplush.eCommerce.business.coupon.CouponLoader;
import com.hhplush.eCommerce.business.coupon.CouponService;
import com.hhplush.eCommerce.business.coupon.UserCouponLoader;
import com.hhplush.eCommerce.business.user.UserLoader;
import com.hhplush.eCommerce.domain.coupon.Coupon;
import com.hhplush.eCommerce.domain.coupon.CouponQuantity;
import com.hhplush.eCommerce.domain.coupon.UserCoupon;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class CouponServiceTest {

    @Mock
    private UserLoader userLoader;
    @Mock
    private CouponLoader couponLoader;
    @Mock
    private UserCouponLoader userCouponLoader;

    @InjectMocks
    private CouponService couponService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Nested
    @DisplayName("UserCoupon 의 issueCoupon 메서드 테스트")
    class IssueCouponTests {

        @Test
        void issueCoupon_성공() {
            // given
            Long couponId = 1L;
            Long userId = 1L;

            Coupon coupon = Coupon.builder().build();
            when(couponLoader.getCouponByCouponId(couponId)).thenReturn(coupon);

            CouponQuantity couponQuantity = CouponQuantity.builder().quantity(1L).build();
            when(couponLoader.checkCouponQuantity(couponId)).thenReturn(couponQuantity);

            UserCoupon userCoupon = UserCoupon.builder().build();
            when(userCouponLoader.issueUserCoupon(coupon, userId)).thenReturn(userCoupon);
            // when
            UserCoupon result = couponService.issueCoupon(couponId, userId);
            // then
            assertEquals(userCoupon, result);
        }

    }
}