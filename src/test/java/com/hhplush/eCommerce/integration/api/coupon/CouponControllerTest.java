package com.hhplush.eCommerce.integration.api.coupon;

import static com.hhplush.eCommerce.common.exception.message.ExceptionMessage.COUPON_ALREADY_EXISTS;
import static com.hhplush.eCommerce.common.exception.message.ExceptionMessage.COUPON_LIMIT_EXCEEDED;
import static com.hhplush.eCommerce.common.exception.message.ExceptionMessage.COUPON_NOT_FOUND;
import static com.hhplush.eCommerce.common.exception.message.ExceptionMessage.INVALID_ID;
import static com.hhplush.eCommerce.common.exception.message.ExceptionMessage.USER_NOT_FOUND;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import com.hhplush.eCommerce.api.coupon.dto.request.RequestIssueCouponDTO;
import com.hhplush.eCommerce.domain.coupon.Coupon;
import com.hhplush.eCommerce.domain.coupon.CouponQuantity;
import com.hhplush.eCommerce.domain.coupon.CouponState;
import com.hhplush.eCommerce.domain.coupon.UserCoupon;
import com.hhplush.eCommerce.domain.user.User;
import com.hhplush.eCommerce.integration.config.IntegrationTest;
import io.restassured.RestAssured;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class CouponControllerTest extends IntegrationTest {

    @Nested
    @DisplayName("[POST] /api/coupons/{couponId}/issued 쿠폰 발급")
    class IssueCoupon {

        @DisplayName("쿠폰 ID가 1보다 작은 경우 실패 INVALID_ID 예외가 발생한다.")
        @Test
        void issueCoupon_InvalidId() throws Exception {
            // given
            var request = new RequestIssueCouponDTO(1L);

            // when & then
            given()
                .baseUri(baseUrl + RestAssured.port)
                .contentType("application/json")
                .body(objectMapper.writeValueAsString(request))
                .when()
                .post("/api/coupons/0/issued")
                .then()
                .log().all()
                .statusCode(400)
                .body("code", equalTo(String.valueOf(HttpStatus.BAD_REQUEST)))
                .body("message", equalTo(INVALID_ID));
        }

        @DisplayName("사용자를 찾을 수 없는 경우 USER_NOT_FOUND 예외가 발생한다.")
        @Test
        void issueCoupon_UserNotFound() throws Exception {
            // given
            var request = new RequestIssueCouponDTO(1L);

            // when & then
            given()
                .baseUri(baseUrl + RestAssured.port)
                .contentType("application/json")
                .body(objectMapper.writeValueAsString(request))
                .when()
                .post("/api/coupons/1/issued")
                .then()
                .log().all()
                .statusCode(404)
                .body("code", equalTo(String.valueOf(HttpStatus.NOT_FOUND)))
                .body("message", equalTo(USER_NOT_FOUND));
        }

        @DisplayName("쿠폰을 찾을 수 없는 경우 COUPON_NOT_FOUND 예외가 발생한다.")
        @Test
        void issueCoupon_CouponNotFound() throws Exception {
            // given
            User user = User.builder()
                .userName("Test User")
                .point(100L)
                .build();
            user = userJPARepository.save(user);

            var request = new RequestIssueCouponDTO(user.getUserId());

            // when & then
            given()
                .baseUri(baseUrl + RestAssured.port)
                .contentType("application/json")
                .body(objectMapper.writeValueAsString(request))
                .when()
                .post("/api/coupons/1/issued")
                .then()
                .log().all()
                .statusCode(404)
                .body("code", equalTo(String.valueOf(HttpStatus.NOT_FOUND)))
                .body("message", equalTo(COUPON_NOT_FOUND));
        }


        @DisplayName("이미 보유한 쿠폰인 경우 Coupon Already Exists 예외가 발생한다.")
        @Test
        void issueCoupon_AlreadyExists() throws Exception {
            // given

            User user = User.builder()
                .userName("Test User")
                .point(100L)
                .build();
            user = userJPARepository.save(user);  // 데이터베이스에 저장
            var request = new RequestIssueCouponDTO(user.getUserId());

            Coupon coupon = Coupon.builder()
                .couponName("Test Coupon")
                .couponState(CouponState.ISSUABLE)
                .build();

            coupon = couponJPARepository.save(coupon);

            UserCoupon userCoupon = UserCoupon.builder().coupon(coupon).userId(user.getUserId())
                .couponUse(false)
                .createAt(LocalDateTime.now()).build();

            userCoupon = userCouponJPARepository.save(userCoupon);

            // when & then
            given()
                .baseUri(baseUrl + RestAssured.port)
                .contentType("application/json")
                .body(objectMapper.writeValueAsString(request))
                .when()
                .post("/api/coupons/" + coupon.getCouponId() + "/issued")
                .then()
                .log().all()
                .statusCode(409)
                .body("code", equalTo(String.valueOf(HttpStatus.CONFLICT)))
                .body("message", equalTo(COUPON_ALREADY_EXISTS));
        }


        @DisplayName("쿠폰 발급 한도를 초과한 경우 Coupon Limit Exceeded 예외가 발생한다.")
        @Test
        void issueCoupon_LimitExceeded() throws Exception {
            // given
            User user = User.builder()
                .userName("Test User")
                .point(100L)
                .build();
            user = userJPARepository.save(user);  // 데이터베이스에 저장

            var request = new RequestIssueCouponDTO(user.getUserId());

            Coupon coupon = Coupon.builder()
                .couponName("Test Coupon")
                .couponState(CouponState.ISSUABLE)
                .build();

            coupon = couponJPARepository.save(coupon);

            CouponQuantity couponQuantity = CouponQuantity.builder()
                .couponId(coupon.getCouponId())
                .quantity(0L)
                .build();

            couponQuantity = couponQuantityJPARepository.save(couponQuantity);
            // when & then
            given()
                .baseUri(baseUrl + RestAssured.port)
                .contentType("application/json")
                .body(objectMapper.writeValueAsString(request))
                .when()
                .post("/api/coupons/" + coupon.getCouponId() + "/issued")
                .then()
                .log().all()
                .statusCode(409)
                .body("code", equalTo(String.valueOf(HttpStatus.CONFLICT)))
                .body("message", equalTo(COUPON_LIMIT_EXCEEDED));
        }

        @DisplayName("쿠폰 발급에 성공한다.")
        @Test
        void issueCoupon_Success() throws Exception {
            // given
            User user = User.builder()
                .userName("Test User")
                .point(100L)
                .build();
            user = userJPARepository.save(user);  // 데이터베이스에 저장

            var request = new RequestIssueCouponDTO(user.getUserId());

            Coupon coupon = Coupon.builder()
                .couponName("Test Coupon")
                .couponState(CouponState.ISSUABLE)
                .build();

            coupon = couponJPARepository.save(coupon);

            CouponQuantity couponQuantity = CouponQuantity.builder()
                .couponId(coupon.getCouponId())
                .quantity(5L)
                .build();

            couponQuantity = couponQuantityJPARepository.save(couponQuantity);
            // when & then
            given()
                .baseUri(baseUrl + RestAssured.port)
                .contentType("application/json")
                .body(objectMapper.writeValueAsString(request))
                .when()
                .post("/api/coupons/" + coupon.getCouponId() + "/issued")
                .then()
                .log().all()
                .statusCode(200)
                .body("userId", equalTo(user.getUserId().intValue()))
                .body("couponId", equalTo(coupon.getCouponId().intValue()))
                .body("couponUse", equalTo(false))
                .body("useAt", equalTo(null));
        }
    }
}
