package com.hhplush.eCommerce.integration.api.user;

import static com.hhplush.eCommerce.common.exception.message.ExceptionMessage.INVALID_ID;
import static com.hhplush.eCommerce.common.exception.message.ExceptionMessage.INVALID_POINT;
import static com.hhplush.eCommerce.common.exception.message.ExceptionMessage.USER_NOT_FOUND;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import com.hhplush.eCommerce.api.user.dto.request.RequestChargeUserPointDTO;
import com.hhplush.eCommerce.domain.coupon.Coupon;
import com.hhplush.eCommerce.domain.coupon.UserCoupon;
import com.hhplush.eCommerce.domain.user.User;
import com.hhplush.eCommerce.integration.config.IntegrationTest;
import io.restassured.RestAssured;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class UserControllerTest extends IntegrationTest {

    @Nested
    @DisplayName("[GET] /api/users 유저 조회")
    class GetUser {

        @DisplayName("유저 ID는 1 보다 작은 경우 실패 BAD_REQUEST 예외가 발생한다.")
        @Test
        void getUser_InvalidId() {
            given()
                .baseUri(baseUrl + RestAssured.port)
                .when()
                .get("/api/users/0")
                .then()
                .statusCode(400)
                .body("code", equalTo(String.valueOf(HttpStatus.BAD_REQUEST)))
                .body("message", equalTo(INVALID_ID));
        }

        @DisplayName("유저 조회시 ID는 1보다 크고 DB에 데이터가 없는 경우 경우 한다.")
        @Test
        void getUser_USER_NOT_FOUND() {
            // given
            // when & then
            given()
                .baseUri(baseUrl + RestAssured.port)
                .when()
                .get("/api/users/" + 1)
                .then()
                .statusCode(404)
                .body("code", equalTo(String.valueOf(HttpStatus.NOT_FOUND)))
                .body("message", equalTo(USER_NOT_FOUND));
        }

        @DisplayName("유저 조회시 ID는 1보다 크고 DB에 데이터가 있는 경우 성공한다.")
        @Test
        void getUser() {
            // given
            User user = User.builder()
                .userName("Test User")
                .point(100L)
                .build();
            user = userJPARepository.save(user);  // 데이터베이스에 저장

            // when & then
            given()
                .baseUri(baseUrl + RestAssured.port)
                .when()
                .get("/api/users/" + user.getUserId())
                .then()
                .statusCode(200)
                .body("userId", equalTo(user.getUserId().intValue()))
                .body("userName", equalTo(user.getUserName()))
                .body("point", equalTo(user.getPoint().intValue()));
        }
    }


    @Nested
    @DisplayName("[POST] /api/users/{userId}/charge 유저 포인트 충전")
    class ChargeUserPoint {

        @DisplayName("유저 ID는 1 보다 작은 경우 실패 BAD_REQUEST 예외가 발생한다.")
        @Test
        void chargeUserPoint_InvalidId() throws Exception {

            // given
            var request = new RequestChargeUserPointDTO(1L);
            given()
                .baseUri(baseUrl + RestAssured.port)
                .contentType("application/json")
                .body(objectMapper.writeValueAsString(request))
                .when()
                .post("/api/users/0/charge")
                .then()
                .log().all()
                .statusCode(400)
                .body("code", equalTo(String.valueOf(HttpStatus.BAD_REQUEST)))
                .body("message", equalTo(INVALID_ID));
        }

        @DisplayName("포인트가 1보다 작은경우 실패 BAD_REQUEST 예외가 발생한다.")
        @Test
        void chargeUserPoint_INVALID_POINT() throws Exception {
            // given
            var request = new RequestChargeUserPointDTO(0L);
            // when & then
            given()
                .baseUri(baseUrl + RestAssured.port)
                .contentType("application/json")
                .body(objectMapper.writeValueAsString(request))
                .when()
                .post("/api/users/" + 1 + "/charge")
                .then()
                .log().all()
                .statusCode(400)
                .body("code", equalTo(String.valueOf(HttpStatus.BAD_REQUEST)))
                .body("message", equalTo(INVALID_POINT));
        }

        @DisplayName("유저 정보가 없는 경우 실패 NOT_FOUND 예외가 발생한다.")
        @Test
        void chargeUserPoint_NOT_FOUND() throws Exception {
            // given
            var request = new RequestChargeUserPointDTO(100L);

            // when & then
            given()
                .baseUri(baseUrl + RestAssured.port)
                .contentType("application/json")
                .body(objectMapper.writeValueAsString(request))
                .when()
                .post("/api/users/" + 1 + "/charge")
                .then()
                .log().all()
                .statusCode(404)
                .body("code", equalTo(String.valueOf(HttpStatus.NOT_FOUND)))
                .body("message", equalTo(USER_NOT_FOUND));
        }

        @DisplayName("유저데이터가 있다면 포인트 충전을 성공한다.")
        @Test
        void chargeUserPoint() throws Exception {
            // given
            var request = new RequestChargeUserPointDTO(100L);
            User user = User.builder()
                .userName("Test User")
                .point(100L)
                .build();
            user = userJPARepository.save(user);  // 데이터베이스에 저장

            // when & then
            given()
                .baseUri(baseUrl + RestAssured.port)
                .contentType("application/json")
                .body(objectMapper.writeValueAsString(request))
                .when()
                .post("/api/users/" + user.getUserId() + "/charge")
                .then()
                .log().all()
                .statusCode(200)
                .body("userId", equalTo(user.getUserId().intValue()))
                .body("userName", equalTo(user.getUserName()))
                .body("point", equalTo(user.getPoint().intValue() + request.point().intValue()));
        }
    }

    @Nested
    @DisplayName("[GET] /api/users/{userId}/coupon 유저 쿠폰 조회")
    class GetUserCoupon {

        @DisplayName("유저 ID는 1 보다 작은 경우 실패 BAD_REQUEST 예외가 발생한다.")
        @Test
        void getUserCoupon_InvalidId() {
            // given
            // when & then
            given()
                .baseUri(baseUrl + RestAssured.port)
                .when()
                .get("/api/users/0/coupon")
                .then()
                .statusCode(400)
                .body("code", equalTo(String.valueOf(HttpStatus.BAD_REQUEST)))
                .body("message", equalTo(INVALID_ID));
        }

        @DisplayName("유저 ID가 DB에 존재하지 않으면 실패 NOT_FOUND 예외가 발생한다.")
        @Test
        void getUserCoupon_USER_NOT_FOUND() {
            // given
            // when & then
            given()
                .baseUri(baseUrl + RestAssured.port)
                .when()
                .get("/api/users/1/coupon")
                .then()
                .statusCode(404)
                .body("code", equalTo(String.valueOf(HttpStatus.NOT_FOUND)))
                .body("message", equalTo(USER_NOT_FOUND));
        }

        @DisplayName("유저 쿠폰이 없다면 빈 배열을 반환한다.")
        @Test
        void getUserCoupon_emptyList() {
            // given
            User user = User.builder()
                .userName("Test User")
                .point(100L)
                .build();
            user = userJPARepository.save(user);  // 데이터베이스에 저장
            // when & then
            given()
                .baseUri(baseUrl + RestAssured.port)
                .when()
                .get("/api/users/" + user.getUserId() + "/coupon")
                .then()
                .statusCode(200)
                .body("size()", equalTo(0));
        }

        @DisplayName("유저 쿠폰을 반환한다.")
        @Test
        void getUserCoupon() {
            // given
            User user = User.builder()
                .userName("Test User")
                .point(100L)
                .build();
            user = userJPARepository.save(user);  // 데이터베이스에 저장

            Coupon coupon = Coupon.builder()
                .couponName("Coupon")
                .discountAmount(100L)
                .build();
            coupon = couponJPARepository.save(coupon);

            UserCoupon userCoupon = UserCoupon.builder()
                .userId(user.getUserId())
                .coupon(coupon)
                .couponUse(false)
                .createAt(LocalDateTime.now())
                .build();
            userCoupon = userCouponJPARepository.save(userCoupon);
            // when & then
            given()
                .baseUri(baseUrl + RestAssured.port)
                .when()
                .get("/api/users/" + user.getUserId() + "/coupon")
                .then()
                .statusCode(200)
                .body("size()", equalTo(1))
                .body("[0].userCouponId", equalTo(userCoupon.getUserCouponId().intValue()))
                .body("[0].userId", equalTo(userCoupon.getUserId().intValue()))
                .body("[0].couponUse", equalTo(userCoupon.getCouponUse()));
        }
    }
}
