package com.hhplush.eCommerce.unit.coupon;

import static com.hhplush.eCommerce.common.exception.message.ExceptionMessage.COUPON_ALREADY_EXISTS;
import static com.hhplush.eCommerce.common.exception.message.ExceptionMessage.COUPON_LIMIT_EXCEEDED;
import static com.hhplush.eCommerce.common.exception.message.ExceptionMessage.COUPON_NOT_FOUND;
import static com.hhplush.eCommerce.common.exception.message.ExceptionMessage.COUPON_USE_ALREADY_EXISTS;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hhplush.eCommerce.common.exception.custom.AlreadyExistsException;
import com.hhplush.eCommerce.common.exception.custom.LimitExceededException;
import com.hhplush.eCommerce.common.exception.custom.ResourceNotFoundException;
import com.hhplush.eCommerce.domain.coupon.Coupon;
import com.hhplush.eCommerce.domain.coupon.CouponQuantity;
import com.hhplush.eCommerce.domain.coupon.CouponService;
import com.hhplush.eCommerce.domain.coupon.ICouponRepository;
import com.hhplush.eCommerce.domain.coupon.UserCoupon;
import com.hhplush.eCommerce.domain.user.User;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
    private ICouponRepository couponRepository;

    @InjectMocks
    private CouponService couponService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Nested
    @DisplayName("CouponService 의 getCouponByCouponId 메서드 테스트")
    class GetCouponByCouponIdTests {

        @DisplayName("존재하는 쿠폰 ID로 쿠폰 정보를 반환한다.")
        @Test
        void getCouponByCouponId_success() {
            // given
            Long couponId = 1L;
            Coupon coupon = Coupon.builder()
                .couponId(couponId)
                .couponName("Discount Coupon")
                .discountAmount(1000L)
                .build();
            when(couponRepository.findById(couponId)).thenReturn(Optional.of(coupon));

            // when
            Coupon result = couponService.getCouponByCouponId(couponId);

            // then
            assertEquals(coupon, result);
        }

        @DisplayName("존재하지 않는 쿠폰 ID로 예외를 발생시킨다.")
        @Test
        void getCouponByCouponId_notFound() {
            // given
            Long couponId = 999L;
            when(couponRepository.findById(couponId)).thenReturn(Optional.empty());

            // when
            ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> couponService.getCouponByCouponId(couponId)
            );

            // then
            assertEquals(COUPON_NOT_FOUND, exception.getMessage());
        }
    }

    @Nested
    @DisplayName("CouponService 의 checkCouponQuantity 메서드 테스트")
    class CheckCouponQuantityTests {

        @DisplayName("쿠폰 수량이 충분한 경우 쿠폰 수량 정보를 반환한다.")
        @Test
        void checkCouponQuantity_success() {
            // given
            Long couponId = 1L;
            CouponQuantity couponQuantity = CouponQuantity.builder()
                .couponId(couponId)
                .quantity(10L)
                .build();
            when(couponRepository.findCouponQuantityByCouponId(couponId))
                .thenReturn(couponQuantity);

            // when
            CouponQuantity result = couponService.checkCouponQuantity(couponId);

            // then
            assertEquals(couponQuantity, result);
        }

        @DisplayName("쿠폰 수량이 부족한 경우 LimitExceededException 예외를 발생시킨다.")
        @Test
        void COUPON_LIMIT_EXCEEDED_LimitExceededException() {
            // given
            Long couponId = 1L;
            CouponQuantity couponQuantity = CouponQuantity.builder()
                .couponId(couponId)
                .quantity(0L)
                .build();
            when(couponRepository.findCouponQuantityByCouponId(couponId))
                .thenReturn(couponQuantity);

            // when
            LimitExceededException exception = assertThrows(
                LimitExceededException.class,
                () -> couponService.checkCouponQuantity(couponId)
            );

            // then
            assertEquals(COUPON_LIMIT_EXCEEDED, exception.getMessage());
        }
    }

    @Nested
    @DisplayName("CouponService 의 issueUserCoupon 메서드 테스트")
    class IssueUserCouponTests {

        @DisplayName("사용자에게 쿠폰 발급을 성공한다.")
        @Test
        void issueUserCoupon_success() {
            // given
            Long couponId = 1L;
            Long userId = 1L;
            User user = User.builder().userId(userId).userName("John Doe").build();
            Coupon coupon = Coupon.builder().couponId(couponId).build();
            CouponQuantity couponQuantity = CouponQuantity.builder().couponId(couponId)
                .quantity(10L).build();
            UserCoupon expectedUserCoupon = new UserCoupon(coupon, userId);

            // when
            UserCoupon result = couponService.issueUserCoupon(coupon, couponQuantity, user);

            // then
            assertEquals(expectedUserCoupon.getCoupon(), result.getCoupon());
            assertEquals(expectedUserCoupon.getUserId(), result.getUserId());
            assertEquals(couponQuantity.getQuantity(), 9L);
            verify(couponRepository).userCouponSave(result);
            verify(couponRepository).couponQuantitySave(couponQuantity);

        }
    }

    @Nested
    @DisplayName("CouponService 의 checkCouponValidity 메서드 테스트")
    class CheckCouponValidityTests {

        @DisplayName("사용자가 해당 쿠폰을 이미 발급받았다면 예외를 발생시킨다.")
        @Test
        void COUPON_ALREADY_EXISTS_AlreadyExistsException() {
            // given
            Long couponId = 1L;
            Long userId = 1L;
            when(couponRepository.findByUserIdAndCouponId(userId, couponId))
                .thenReturn(Optional.of(UserCoupon.builder().build()));

            // when
            AlreadyExistsException exception = assertThrows(
                AlreadyExistsException.class,
                () -> couponService.checkCouponValidity(userId, couponId)
            );

            // then
            assertEquals(COUPON_ALREADY_EXISTS, exception.getMessage());
        }


        @DisplayName("사용자가 해당 쿠폰을 발급받은 이력이 없다면 아무것도 하지 않는다.")
        @Test
        void checkCouponValidity_success() {
            // given
            Long couponId = 1L;
            Long userId = 1L;
            when(couponRepository.findByUserIdAndCouponId(userId, couponId))
                .thenReturn(Optional.empty());

            // when & then
            couponService.checkCouponValidity(userId, couponId);

            verify(couponRepository).findByUserIdAndCouponId(userId, couponId);
        }
    }

    @Nested
    @DisplayName("CouponService 의 useUserCoupon 메서드 테스트")
    class UseUserCouponTests {

        @DisplayName("사용자 쿠폰을 사용 처리한다.")
        @Test
        void useUserCoupon_success() {
            // given
            UserCoupon userCoupon = UserCoupon.builder()
                .userCouponId(1L)
                .couponUse(false)
                .build();
            Boolean couponUse = true;

            // when
            UserCoupon result = couponService.useUserCoupon(userCoupon, couponUse);

            // then
            assertEquals(couponUse, result.getCouponUse());
            assertNotNull(result.getUseAt());
            verify(couponRepository).userCouponSave(result);
        }

        @DisplayName("사용자 쿠폰을 사용하지 않는 상태로 처리한다.")
        @Test
        void useUserCoupon_notUsed() {
            // given
            UserCoupon userCoupon = UserCoupon.builder()
                .userCouponId(1L)
                .couponUse(true)
                .useAt(LocalDateTime.now())
                .build();
            Boolean couponUse = false;

            // when
            UserCoupon result = couponService.useUserCoupon(userCoupon, couponUse);

            // then
            assertEquals(couponUse, result.getCouponUse());
            assertNull(result.getUseAt());
            verify(couponRepository).userCouponSave(result);
        }
    }

    @Nested
    @DisplayName("CouponService 의 CheckUserCouponIsUsed 메서드 테스트")
    class CheckUserCouponIsUsedTests {

        @DisplayName("사용자 쿠폰이 이미 사용된 경우 예외를 발생시킨다.")
        @Test
        void COUPON_USE_ALREADY_EXISTS_ResourceNotFoundException() {
            // given
            UserCoupon userCoupon = UserCoupon.builder()
                .couponUse(true)
                .build();

            // when
            AlreadyExistsException exception = assertThrows(
                AlreadyExistsException.class,
                () -> couponService.CheckUserCouponIsUsed(userCoupon)
            );

            // then
            assertEquals(exception.getMessage(), COUPON_USE_ALREADY_EXISTS);
        }

        @DisplayName("사용자 쿠폰이 사용되지 않은 경우 오류가 발생하지 않는다.")
        @Test
        void checkUserCouponIsUsed_notUsed() {
            // given
            UserCoupon userCoupon = UserCoupon.builder()
                .couponUse(false)
                .build();

            // when & then
            assertDoesNotThrow(() -> couponService.CheckUserCouponIsUsed(userCoupon));
        }
    }

    @Nested
    @DisplayName("CouponService 의 getUserCouponByUserCouponId 메서드 테스트")
    class GetUserCouponByUserCouponIdTests {

        @DisplayName("존재하는 사용자 쿠폰 ID로 사용자 쿠폰을 반환한다.")
        @Test
        void getUserCouponByUserCouponId_success() {
            // given
            Long userCouponId = 1L;
            UserCoupon userCoupon = UserCoupon.builder()
                .userCouponId(userCouponId)
                .build();
            when(couponRepository.userCouponfindById(userCouponId))
                .thenReturn(Optional.of(userCoupon));

            // when
            UserCoupon result = couponService.getUserCouponByUserCouponId(userCouponId);

            // then
            assertEquals(userCoupon, result);
        }

        @DisplayName("존재하지 않는 사용자 쿠폰 ID로 예외를 발생시킨다.")
        @Test
        void COUPON_NOT_FOUND_ResourceNotFoundException() {
            // given
            Long userCouponId = 1L;
            when(couponRepository.userCouponfindById(userCouponId))
                .thenReturn(Optional.empty());

            // when
            ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> couponService.getUserCouponByUserCouponId(userCouponId)
            );

            // then
            assertEquals(COUPON_NOT_FOUND, exception.getMessage());
        }
    }

    @Nested
    @DisplayName("CouponService 의 getUserCouponListByUserId 메서드 테스트")
    class GetUserCouponListByUserIdTests {

        @DisplayName("사용자의 쿠폰 목록을 성공적으로 반환한다.")
        @Test
        void getUserCouponListByUserId_success() {
            // given
            Long userId = 1L;
            List<UserCoupon> userCouponList = new ArrayList<>();
            for (int i = 0; i < 3; i++) {
                userCouponList.add(UserCoupon.builder().userCouponId((long) i).build());
            }
            when(couponRepository.findUserCouponByUserId(userId))
                .thenReturn(userCouponList);

            // when
            List<UserCoupon> result = couponService.getUserCouponListByUserId(userId);

            // then
            assertEquals(userCouponList, result);
        }

        @DisplayName("사용자의 쿠폰 목록이 없으면 빈 리스트를 반환한다.")
        @Test
        void getUserCouponListByUserId_success_emptyList() {
            // given
            Long userId = 1L;
            List<UserCoupon> userCouponList = new ArrayList<>();
            when(couponRepository.findUserCouponByUserId(userId))
                .thenReturn(userCouponList);

            // when
            List<UserCoupon> result = couponService.getUserCouponListByUserId(userId);

            // then
            assertTrue(result.isEmpty());
        }
    }
}
