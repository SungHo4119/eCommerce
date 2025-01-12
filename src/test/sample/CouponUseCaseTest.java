package com.hhplush.eCommerce.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.hhplush.eCommerce.domain.coupon.CouponService;
import com.hhplush.eCommerce.business.coupon.CouponUseCase;
import com.hhplush.eCommerce.domain.coupon.UserCouponService;
import com.hhplush.eCommerce.domain.coupon.Coupon;
import com.hhplush.eCommerce.domain.coupon.CouponQuantity;
import com.hhplush.eCommerce.domain.coupon.UserCoupon;
import com.hhplush.eCommerce.domain.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class CouponUseCaseTest {

    @Mock
    private UserService userService;
    @Mock
    private CouponService couponService;
    @Mock
    private UserCouponService userCouponService;

    @InjectMocks
    private CouponUseCase couponUseCase;

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
            when(couponService.getCouponByCouponId(couponId)).thenReturn(coupon);

            CouponQuantity couponQuantity = CouponQuantity.builder().quantity(1L).build();
            when(couponService.checkCouponQuantity(couponId)).thenReturn(couponQuantity);

            UserCoupon userCoupon = UserCoupon.builder().build();
            when(userCouponService.issueUserCoupon(coupon, userId)).thenReturn(userCoupon);
            // when
            UserCoupon result = couponUseCase.issueCoupon(couponId, userId);
            // then
            assertEquals(userCoupon, result);
        }

    }
}