package com.hhplush.eCommerce.unit;

import static com.hhplush.eCommerce.common.exception.message.ExceptionMessage.COUPON_ALREADY_EXISTS;
import static com.hhplush.eCommerce.common.exception.message.ExceptionMessage.COUPON_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.hhplush.eCommerce.business.coupon.ICouponRepository;
import com.hhplush.eCommerce.business.coupon.UserCouponLoader;
import com.hhplush.eCommerce.business.user.IUserRepository;
import com.hhplush.eCommerce.common.exception.custom.AlreadyExistsException;
import com.hhplush.eCommerce.common.exception.custom.ResourceNotFoundException;
import com.hhplush.eCommerce.domain.coupon.Coupon;
import com.hhplush.eCommerce.domain.coupon.UserCoupon;
import com.hhplush.eCommerce.domain.coupon.UserCouponInfo;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class UserCouponLoaderTest {

    @Mock
    private IUserRepository userRepository;

    @Mock
    private ICouponRepository couponRepository;

    @InjectMocks
    private UserCouponLoader userCouponLoader;

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
            UserCouponInfo userCouponInfo = UserCouponInfo.builder().build();
            List<UserCouponInfo> userCouponList = List.of(userCouponInfo);
            when(userRepository.findUserCouponByUserId(userId)).thenReturn(userCouponList);

            // when
            List<UserCouponInfo> result = userCouponLoader.getUserCouponListByUserId(userId);

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
                () -> userCouponLoader.getUserCouponByUserCouponId(userCouponId));

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
            UserCoupon result = userCouponLoader.getUserCouponByUserCouponId(userCouponId);

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
                () -> userCouponLoader.checkCouponValidity(userId, couponId));

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
            userCouponLoader.checkCouponValidity(userId, couponId);
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
                .coupon(coupon)
                .userId(userId)
                .build();

            // when
            UserCoupon result = userCouponLoader.issueUserCoupon(coupon, userId);

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
            UserCoupon result = userCouponLoader.useUserCoupon(userCoupon, couponUse);

            // then
            assertEquals(couponUse, result.getCouponUse());
        }
    }
}
