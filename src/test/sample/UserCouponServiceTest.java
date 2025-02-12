package com.hhplush.eCommerce.unit;

import static com.hhplush.eCommerce.common.exception.message.ExceptionMessage.COUPON_ALREADY_EXISTS;
import static com.hhplush.eCommerce.common.exception.message.ExceptionMessage.COUPON_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.hhplush.eCommerce.domain.coupon.ICouponRepository;
import com.hhplush.eCommerce.domain.coupon.UserCouponService;
import com.hhplush.eCommerce.common.exception.custom.AlreadyExistsException;
import com.hhplush.eCommerce.common.exception.custom.ResourceNotFoundException;
import com.hhplush.eCommerce.domain.coupon.Coupon;
import com.hhplush.eCommerce.domain.coupon.UserCoupon;
import com.hhplush.eCommerce.domain.user.IUserRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class UserCouponServiceTest {

    @Mock
    private IUserRepository userRepository;

    @Mock
    private ICouponRepository couponRepository;

    @InjectMocks
    private UserCouponService userCouponService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Nested
    @DisplayName("UserCouponLoader 의 getUserCouponListByUserId 메서드 테스트")
    class GetUserCouponListByUserIdTests {

        @Test
        void getUserCouponListByUserId_성공() {
            // given
            Long userId = 1L;
            UserCoupon userCoupon = UserCoupon.builder().build();
            List<UserCoupon> userCouponList = List.of(userCoupon);
            when(userRepository.findUserCouponByUserId(userId)).thenReturn(userCouponList);

            // when
            List<UserCoupon> result = userCouponService.getUserCouponListByUserId(userId);

            // then
            assertEquals(userCouponList, result);
        }
    }

    @Nested
    @DisplayName("UserCouponLoader 의 GetUserCouponByUserCouponIdTests 메서드 테스트")
    class GetUserCouponByUserCouponIdTests {

        @Test
        void COUPON_NOT_FOUND_ResourceNotFoundException() {
            // given
            Long userCouponId = 1L;
            when(couponRepository.userCouponfindById(userCouponId)).thenReturn(Optional.empty());

            // when
            ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> userCouponService.getUserCouponByUserCouponId(userCouponId));

            // then
            assertEquals(COUPON_NOT_FOUND, exception.getMessage());
        }

        @Test
        void getUserCouponByUserCouponId_성공() {
            // given
            Long userCouponId = 1L;
            UserCoupon userCoupon = UserCoupon.builder().userCouponId(userCouponId).build();
            when(couponRepository.userCouponfindById(userCouponId)).thenReturn(
                Optional.of(userCoupon));

            // when
            UserCoupon result = userCouponService.getUserCouponByUserCouponId(userCouponId);

            // then
            assertEquals(userCoupon, result);
        }
    }

    @Nested
    @DisplayName("UserCouponLoader 의 checkCouponValidity 메서드 테스트")
    class CheckCouponValidityTests {

        @Test
        void COUPON_ALREADY_EXISTS_AlreadyExistsException() {
            // given
            Long userId = 1L;
            Long couponId = 1L;
            UserCoupon userCoupon = UserCoupon.builder().userCouponId(1L).userId(userId)
                .couponId(1L).build();
            when(couponRepository.findByUserIdAndCouponId(userId, couponId))
                .thenReturn(Optional.of(userCoupon));

            // when
            AlreadyExistsException exception = assertThrows(AlreadyExistsException.class,
                () -> userCouponService.checkCouponValidity(userId, couponId));

            // then
            assertEquals(COUPON_ALREADY_EXISTS, exception.getMessage());
        }

        @Test
        void checkCouponValidity_성공() {
            // given
            Long userId = 1L;
            Long couponId = 1L;
            when(couponRepository.findByUserIdAndCouponId(userId, couponId)).thenReturn(
                Optional.empty());

            // when & then
            userCouponService.checkCouponValidity(userId, couponId);
        }
    }

    @Nested
    @DisplayName("UserCouponLoader 의 issueUserCoupon 메서드 테스트")
    class IssueUserCouponTests {

        @Test
        void issueUserCoupon_성공() {
            // given
            Long userId = 1L;
            Coupon coupon = Coupon.builder().couponId(1L).build();
            UserCoupon userCoupon = UserCoupon.builder()
                .couponId(coupon.getCouponId())
                .userId(userId)
                .build();

            // when
            UserCoupon result = userCouponService.issueUserCoupon(coupon, userId);

            // then
            assertEquals(userCoupon.getCoupon(), result.getCoupon());
            assertEquals(userCoupon.getUserId(), result.getUserId());
        }
    }

    @Nested
    class UseUserCouponTests {

        @Test
        void useUserCoupon_성공() {
            // given
            UserCoupon userCoupon = UserCoupon.builder().couponUse(false).build();
            Boolean couponUse = true;

            // when
            UserCoupon result = userCouponService.useUserCoupon(userCoupon, couponUse);

            // then
            assertEquals(couponUse, result.getCouponUse());
        }
    }
}
