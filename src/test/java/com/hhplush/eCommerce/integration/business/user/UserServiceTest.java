package com.hhplush.eCommerce.integration.business.user;

import static com.hhplush.eCommerce.common.exception.message.ExceptionMessage.USER_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.hhplush.eCommerce.common.exception.custom.ResourceNotFoundException;
import com.hhplush.eCommerce.domain.coupon.Coupon;
import com.hhplush.eCommerce.domain.coupon.UserCoupon;
import com.hhplush.eCommerce.domain.coupon.UserCouponInfo;
import com.hhplush.eCommerce.domain.user.User;
import com.hhplush.eCommerce.integration.config.IntegrationTest;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;


public class UserServiceTest extends IntegrationTest {

    @Nested
    @DisplayName("유저 조회")
    @Transactional
    class GetUser {

        @Test
        void 유저조회시_ID가존재하지않으면_실패_USER_NOT_FOUND() {
            // given
            // when
            ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> userService.getUser(1L));
            // then
            assertEquals(USER_NOT_FOUND, exception.getMessage());
        }

        @Test
        void 유저조회_성공() {
            // given
            User user = User.builder()
                .userName("Test User")
                .point(100L)
                .build();
            user = userJPARepository.save(user);
            // when
            User result = userService.getUser(user.getUserId());
            // then
            assertEquals(user.getPoint(), result.getPoint());
            assertEquals(user.getUserId(), result.getUserId());
            assertEquals(user.getUserName(), result.getUserName());
        }
    }

    @Nested
    @DisplayName("유저 포인트 충전")
    @Transactional
    class IncreaseUserPointTest {

        @Test
        void 유저포인트충전_ID가존재하지않으면_실패_USER_NOT_FOUND() {
            // given
            // when
            ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> userService.getUserCoupon(1L));
            // then
            assertEquals(USER_NOT_FOUND, exception.getMessage());
        }

        @Test
        void 유저포인트충전_성공() {

            // given
            User user = User.builder()
                .userName("Test User")
                .point(100L)
                .build();
            user = userJPARepository.save(user);
            // when
            userService.chargeUserPoint(user.getUserId(), 100L);
            User result = userService.getUser(user.getUserId());
            // then
            assertEquals(200L, result.getPoint());
        }
    }


    @Nested
    @DisplayName("유저 쿠폰 조회")
    @Transactional
    class GetUserCoupon {

        @Test
        void 유저포인트충전_ID가존재하지않으면_실패_USER_NOT_FOUND() {
            // given
            // when
            ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> userService.getUserCoupon(1L));
            // then
            assertEquals(USER_NOT_FOUND, exception.getMessage());
        }

        @Test
        void 유저포인트충전_성공() {

            // given
            User user = User.builder()
                .userName("Test User")
                .point(100L)
                .build();
            user = userJPARepository.save(user);

            Coupon coupon = Coupon.builder()
                .couponName("Test Coupon")
                .discountAmount(100L)
                .build();

            coupon = couponJPARepository.save(coupon);

            UserCoupon userCoupon = UserCoupon.builder()
                .couponId(coupon.getCouponId())
                .userId(user.getUserId())
                .couponUse(false)
                .build();

            userCoupon = userCouponJPARepository.save(userCoupon);

            // when
            List<UserCouponInfo> userCouponInfoList = userService.getUserCoupon(user.getUserId());
            // then
            assertEquals(userCouponInfoList.get(0).getUserId(), user.getUserId());
            assertEquals(userCouponInfoList.get(0).getCouponUse(), userCoupon.getCouponUse());
            assertEquals(userCouponInfoList.get(0).getUserCouponId(), userCoupon.getUserCouponId());

            assertEquals(userCouponInfoList.get(0).getCoupon().getCouponId(), coupon.getCouponId());
        }
    }

}
