package com.hhplush.eCommerce.integration.api.order;

import static com.hhplush.eCommerce.common.exception.message.ExceptionMessage.COUPON_USE_ALREADY_EXISTS;
import static com.hhplush.eCommerce.common.exception.message.ExceptionMessage.INVALID_ID;
import static com.hhplush.eCommerce.common.exception.message.ExceptionMessage.INVALID_QUANTITY;
import static com.hhplush.eCommerce.common.exception.message.ExceptionMessage.PRODUCT_LIMIT_EXCEEDED;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import com.hhplush.eCommerce.api.order.dto.request.RequestCreateOrderDTO;
import com.hhplush.eCommerce.api.order.dto.request.RequestCreateOrderDTO.RequestProducts;
import com.hhplush.eCommerce.domain.coupon.Coupon;
import com.hhplush.eCommerce.domain.coupon.UserCoupon;
import com.hhplush.eCommerce.domain.product.Product;
import com.hhplush.eCommerce.domain.product.ProductQuantity;
import com.hhplush.eCommerce.domain.product.ProductState;
import com.hhplush.eCommerce.domain.user.User;
import com.hhplush.eCommerce.integration.config.IntegrationTest;
import io.restassured.RestAssured;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class OrderControllerTest extends IntegrationTest {

    @Nested
    @DisplayName("[POST] /api/orders 주문 생성")
    class CreateOrder {

        @DisplayName("ID가 1보다 작은 경우 실패 INVALID_ID 예외가 발생한다.")
        @Test
        void createOrder_InvalidUserId() throws Exception {
            // given
            RequestCreateOrderDTO request = new RequestCreateOrderDTO(
                0L,
                null,
                List.of(new RequestProducts(1L, 2L))
            );

            // when & then
            given()
                .baseUri(baseUrl + RestAssured.port)
                .contentType("application/json")
                .body(objectMapper.writeValueAsString(request))
                .when()
                .post("/api/orders")
                .then()
                .log().all()
                .statusCode(400)
                .body("code", equalTo(String.valueOf(HttpStatus.BAD_REQUEST)))
                .body("message", equalTo(INVALID_ID));
        }

        @DisplayName("userCouponId가 1보다 작은경우 경우 실패 INVALID_ID 예외가 발생한다.")
        @Test
        void createOrder_InvalidUserCouponId() throws Exception {
            // given
            RequestCreateOrderDTO request = new RequestCreateOrderDTO(
                1L,
                0L,
                List.of(new RequestProducts(1L, 2L))
            );

            // when & then
            given()
                .baseUri(baseUrl + RestAssured.port)
                .contentType("application/json")
                .body(objectMapper.writeValueAsString(request))
                .when()
                .post("/api/orders")
                .then()
                .log().all()
                .statusCode(400)
                .body("code", equalTo(String.valueOf(HttpStatus.BAD_REQUEST)))
                .body("message", equalTo(INVALID_ID));
        }

        @DisplayName("유효하지 않은 productId로 주문 생성에 실패한다.")
        @Test
        void createOrder_InvalidProductId() throws Exception {
            // given
            RequestCreateOrderDTO request = new RequestCreateOrderDTO(
                1L,
                1L,
                List.of(new RequestProducts(0L, 2L)) // Invalid productId
            );

            // when & then
            given()
                .baseUri(baseUrl + RestAssured.port)
                .contentType("application/json")
                .body(objectMapper.writeValueAsString(request))
                .when()
                .post("/api/orders")
                .then()
                .log().all()
                .statusCode(400)
                .body("code", equalTo(String.valueOf(HttpStatus.BAD_REQUEST)))
                .body("message", equalTo(INVALID_ID));
        }

        @DisplayName("유효하지 않은 quantity로 주문 생성에 실패한다.")
        @Test
        void createOrder_InvalidQuantity() throws Exception {
            // given

            RequestCreateOrderDTO request = new RequestCreateOrderDTO(
                1L,
                1L,
                List.of(new RequestProducts(1L, 0L)) // Invalid quantity
            );

            // when & then
            given()
                .baseUri(baseUrl + RestAssured.port)
                .contentType("application/json")
                .body(objectMapper.writeValueAsString(request))
                .when()
                .post("/api/orders")
                .then()
                .log().all()
                .statusCode(400)
                .body("code", equalTo(String.valueOf(HttpStatus.BAD_REQUEST)))
                .body("message", equalTo(INVALID_QUANTITY));
        }

        // 비즈니스 로직 실패 테스트 =====================================

        @DisplayName("사용한 쿠폰인 경우 주문 생성에 실패한다.")
        @Test
        void createOrder_COUPON_USE_ALREADY_EXISTS() throws Exception {
            // given
            User user = User.builder()
                .userName("Test User")
                .point(100L)
                .build();
            user = userJPARepository.save(user);

            Coupon coupon = Coupon.builder()
                .couponName("할인 쿠폰")
                .discountAmount(50L)
                .build();

            coupon = couponJPARepository.save(coupon);

            UserCoupon userCoupon = UserCoupon.builder()
                .userId(user.getUserId())
                .coupon(coupon)
                .couponUse(true)
                .build();

            userCoupon = userCouponJPARepository.save(userCoupon);

            Product product = Product.builder()
                .productName("Test Product")
                .price(100L)
                .productState(ProductState.IN_STOCK)
                .build();
            product = productJPARepository.save(product);

            ProductQuantity productQuantity = ProductQuantity.builder()
                .productId(product.getProductId())
                .quantity(1L) // 재고 부족
                .build();
            productQuantity = productQuantityJPARepository.save(productQuantity);

            RequestCreateOrderDTO request = new RequestCreateOrderDTO(
                user.getUserId(),
                userCoupon.getUserCouponId(),
                List.of(new RequestProducts(product.getProductId(), 2L))
            );

            // when & then
            given()
                .baseUri(baseUrl + RestAssured.port)
                .contentType("application/json")
                .body(objectMapper.writeValueAsString(request))
                .when()
                .post("/api/orders")
                .then()
                .log().all()
                .statusCode(409)
                .body("code", equalTo(String.valueOf(HttpStatus.CONFLICT)))
                .body("message", equalTo(COUPON_USE_ALREADY_EXISTS));
        }


        @DisplayName("재고가 부족한 경우 주문 생성에 실패한다.")
        @Test
        void createOrder_ProductLimitExceeded() throws Exception {
            // given
            User user = User.builder()
                .userName("Test User")
                .point(100L)
                .build();
            user = userJPARepository.save(user);

            Coupon coupon = Coupon.builder()
                .couponName("할인 쿠폰")
                .discountAmount(50L)
                .build();

            coupon = couponJPARepository.save(coupon);

            UserCoupon userCoupon = UserCoupon.builder()
                .userId(user.getUserId())
                .coupon(coupon)
                .couponUse(false)
                .build();

            userCoupon = userCouponJPARepository.save(userCoupon);

            Product product = Product.builder()
                .productName("Test Product")
                .price(100L)
                .productState(ProductState.IN_STOCK)
                .build();
            product = productJPARepository.save(product);

            ProductQuantity productQuantity = ProductQuantity.builder()
                .productId(product.getProductId())
                .quantity(1L) // 재고 부족
                .build();
            productQuantity = productQuantityJPARepository.save(productQuantity);

            RequestCreateOrderDTO request = new RequestCreateOrderDTO(
                user.getUserId(),
                userCoupon.getUserCouponId(),
                List.of(new RequestProducts(product.getProductId(), 2L)) // 요청량 > 재고량
            );

            // when & then
            given()
                .baseUri(baseUrl + RestAssured.port)
                .contentType("application/json")
                .body(objectMapper.writeValueAsString(request))
                .when()
                .post("/api/orders")
                .then()
                .log().all()
                .statusCode(409)
                .body("code", equalTo(String.valueOf(HttpStatus.CONFLICT)))
                .body("message", equalTo(PRODUCT_LIMIT_EXCEEDED));
        }

        // 최종 성공 테스트 =====================================

        @DisplayName("유효한 요청으로 주문 생성에 성공한다.")
        @Test
        void createOrder_Success() throws Exception {
            // given
            User user = User.builder()
                .userName("Test User")
                .point(100L)
                .build();
            user = userJPARepository.save(user);

            Coupon coupon = Coupon.builder()
                .couponName("할인 쿠폰")
                .discountAmount(50L)
                .build();

            coupon = couponJPARepository.save(coupon);

            UserCoupon userCoupon = UserCoupon.builder()
                .userId(user.getUserId())
                .coupon(coupon)
                .couponUse(false)
                .build();

            userCoupon = userCouponJPARepository.save(userCoupon);

            Product product = Product.builder()
                .productName("Test Product")
                .price(100L)
                .productState(ProductState.IN_STOCK)
                .build();
            product = productJPARepository.save(product);

            ProductQuantity productQuantity = ProductQuantity.builder()
                .productId(product.getProductId())
                .quantity(1L) // 재고 부족
                .build();
            productQuantity = productQuantityJPARepository.save(productQuantity);

            RequestCreateOrderDTO request = new RequestCreateOrderDTO(
                user.getUserId(),
                userCoupon.getUserCouponId(),
                List.of(new RequestProducts(product.getProductId(), 1L))
            );

            // when & then
            given()
                .baseUri(baseUrl + RestAssured.port)
                .contentType("application/json")
                .body(objectMapper.writeValueAsString(request))
                .when()
                .post("/api/orders")
                .then()
                .log().all()
                .statusCode(200)
                .body("userId", equalTo(user.getUserId().intValue()))
                .body("orderStatus", equalTo("PENDING"));
        }
    }
}
