package com.hhplush.eCommerce.unit;

import static com.hhplush.eCommerce.common.exception.message.ExceptionMessage.COUPON_LIMIT_EXCEEDED;
import static com.hhplush.eCommerce.common.exception.message.ExceptionMessage.COUPON_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.hhplush.eCommerce.domain.coupon.CouponService;
import com.hhplush.eCommerce.domain.coupon.ICouponRepository;
import com.hhplush.eCommerce.common.exception.custom.LimitExceededException;
import com.hhplush.eCommerce.common.exception.custom.ResourceNotFoundException;
import com.hhplush.eCommerce.domain.coupon.Coupon;
import com.hhplush.eCommerce.domain.coupon.CouponQuantity;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class CouponServiceTest {

    @Mock
    ICouponRepository couponRepository;

    @InjectMocks
    private CouponService couponService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Nested
    @DisplayName("CouponLoader 의 getCouponByCouponId 메서드 테스트")
    class GetCouponByCouponId {

        @Test
        void COUPON_NOT_FOUND_ResourceNotFoundException() {
            // given
            Long couponId = 1L;
            when(couponRepository.findById(couponId)).thenReturn(Optional.empty());
            // when
            ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> couponService.getCouponByCouponId(couponId));
            // then
            assertEquals(COUPON_NOT_FOUND, exception.getMessage());
        }

        @Test
        void getCouponByCouponId_성공() {
            // given
            Long couponId = 1L;
            Coupon coupon = Coupon.builder().couponId(couponId).build();
            when(couponRepository.findById(couponId)).thenReturn(Optional.of(coupon));
            // when
            Coupon result = couponService.getCouponByCouponId(couponId);
            // then
            assertEquals(coupon, result);
        }
    }

    @Nested
    @DisplayName("CouponLoader 의 checkCouponQuantity 메서드 테스트")
    class CheckCouponQuantityTests {

        @Test
        void COUPON_LIMIT_EXCEEDED_LimitExceededException() {
            // given
            Long couponId = 1L;
            CouponQuantity couponQuantity = CouponQuantity.builder().quantity(0L).build();
            when(couponRepository.findCouponQuantityByCouponId(couponId)).thenReturn(
                couponQuantity);
            // when
            LimitExceededException exception = assertThrows(LimitExceededException.class,
                () -> couponService.checkCouponQuantity(couponId));
            // then
            assertEquals(COUPON_LIMIT_EXCEEDED, exception.getMessage());
        }

        @Test
        void checkCouponQuantity_성공() {
            // given
            Long couponId = 1L;
            CouponQuantity couponQuantity = CouponQuantity.builder().quantity(5L).build();
            when(couponRepository.findCouponQuantityByCouponId(couponId)).thenReturn(
                couponQuantity);
            // when
            CouponQuantity result = couponService.checkCouponQuantity(couponId);
            // then
            assertEquals(couponQuantity, result);
        }
    }

    @Nested
    @DisplayName("CouponLoader 의 decreaseCouponQuantity 메서드 테스트")
    class DecreaseCouponQuantityTests {

        @Test
        void COUPON_LIMIT_EXCEEDED_LimitExceededException() {
            // given
            CouponQuantity couponQuantity = CouponQuantity.builder().quantity(0L).build();
            // when
            LimitExceededException exception = assertThrows(LimitExceededException.class,
                () -> couponService.decreaseCouponQuantity(couponQuantity));
            // then
            assertEquals(COUPON_LIMIT_EXCEEDED, exception.getMessage());
        }

        @Test
        void decreaseCouponQuantity_성공() {
            // given
            CouponQuantity couponQuantity = CouponQuantity.builder().quantity(1L).build();
            // when
            couponService.decreaseCouponQuantity(couponQuantity);
            // then
        }
    }
}
