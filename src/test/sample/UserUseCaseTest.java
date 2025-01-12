package com.hhplush.eCommerce.unit;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.hhplush.eCommerce.domain.coupon.UserCouponService;
import com.hhplush.eCommerce.business.user.UserUseCase;
import com.hhplush.eCommerce.domain.coupon.Coupon;
import com.hhplush.eCommerce.domain.coupon.UserCoupon;
import com.hhplush.eCommerce.domain.user.User;
import com.hhplush.eCommerce.domain.user.UserService;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class UserUseCaseTest {


    @Mock
    private UserService userService;
    @Mock
    private UserCouponService userCouponService;

    @InjectMocks
    private UserUseCase userUseCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Nested
    @DisplayName("UserService 의 getUser 메서드 테스트")
    class GetUserTests {

        @Test
        void 유저조회_성공() {
            // Given
            Long userId = 1L;
            User user = User.builder()
                .userId(userId)
                .userName("Test User")
                .point(100L)
                .build();
            when(userService.getUserByUserId(userId)).thenReturn(user);

            // When
            User result = userUseCase.getUser(userId);

            // Then
            assertEquals(user, result);
        }
    }

    @Nested
    @DisplayName("UserService 의 chargeUserPoint 메서드 테스트")
    class ChargeUserPointTests {

        @Test
        void chargeUserPoint_성공() {
            // Given
            Long userId = 1L;
            Long userPoint = 100L;
            Long addPoint = 100L;
            User user = User.builder()
                .userId(userId)
                .userName("Test User")
                .point(100L)
                .build();

            when(userService.getUserByUserId(userId)).thenReturn(user);
            when(userService.saveUser(user)).thenReturn(user);

            // When
            User result = userUseCase.chargeUserPoint(userId, addPoint);

            // Then
            assertEquals(user.getUserId(), result.getUserId());
            assertEquals(result.getPoint(), userPoint + addPoint);

        }
    }

    @Nested
    @DisplayName("UserService 의 getUserCoupon 메서드 테스트")
    class GetUserCouponTests {

        @Test
        void getUserCoupon_성공() {
            // Given
            Long userId = 1L;
            List<UserCoupon> userCouponList = List.of(
                UserCoupon.builder()
                    .userCouponId(1L)
                    .userId(1L)
                    .couponUse(false)
                    .useAt(null)
                    .createAt(LocalDateTime.parse("2021-10-10T00:00:00"))
                    .coupon(Coupon.builder()
                        .couponId(1L)
                        .couponName("쿠폰")
                        .discountAmount(100L)
                        .build())
                    .build()
            );
            when(userService.getUserByUserId(userId)).thenReturn(
                User.builder().userId(userId).build());
            when(userCouponService.getUserCouponListByUserId(userId)).thenReturn(userCouponList);
            //when
            userUseCase.getUserCoupon(userId);
            //Then
            assertEquals(userCouponList, userUseCase.getUserCoupon(userId));
        }
    }

}