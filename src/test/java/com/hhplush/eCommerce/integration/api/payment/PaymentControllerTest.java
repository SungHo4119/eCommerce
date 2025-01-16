package com.hhplush.eCommerce.integration.api.payment;

import static com.hhplush.eCommerce.common.exception.message.ExceptionMessage.INSUFFICIENT_BALANCE;
import static com.hhplush.eCommerce.common.exception.message.ExceptionMessage.INVALID_ID;
import static com.hhplush.eCommerce.common.exception.message.ExceptionMessage.ORDER_NOT_FOUND;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.equalTo;

import com.hhplush.eCommerce.api.payment.dto.request.RequestCreatePaymentDTO;
import com.hhplush.eCommerce.domain.order.Order;
import com.hhplush.eCommerce.domain.order.OrderProduct;
import com.hhplush.eCommerce.domain.order.OrderState;
import com.hhplush.eCommerce.domain.product.Product;
import com.hhplush.eCommerce.domain.product.ProductQuantity;
import com.hhplush.eCommerce.domain.product.ProductState;
import com.hhplush.eCommerce.domain.user.User;
import com.hhplush.eCommerce.integration.config.IntegrationTest;
import io.restassured.RestAssured;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class PaymentControllerTest extends IntegrationTest {

    @Nested
    @DisplayName("[POST] /api/payments 결제 생성")
    class CreatePayment {

        // 입력 검증 실패 =====================================

        @DisplayName("ID가 1보다 작은 경우 실패 INVALID_ID 예외가 발생한다.")
        @Test
        void createPayment_InvalidOrderId() throws Exception {
            // given
            RequestCreatePaymentDTO request = new RequestCreatePaymentDTO(0L);

            // when & then
            given()
                .baseUri(baseUrl + RestAssured.port)
                .contentType("application/json")
                .body(objectMapper.writeValueAsString(request))
                .when()
                .post("/api/payments")
                .then()
                .log().all()
                .statusCode(400)
                .body("code", equalTo(String.valueOf(HttpStatus.BAD_REQUEST)))
                .body("message", equalTo(INVALID_ID));
        }

        // 비즈니스 로직 실패 =====================================

        @DisplayName("주문이 존재하지 않을 경우 ORDER_NOT_FOUND 예외가 발생한다.")
        @Test
        void createPayment_OrderNotFound() throws Exception {
            // given
            RequestCreatePaymentDTO request = new RequestCreatePaymentDTO(1L);

            // when & then
            given()
                .baseUri(baseUrl + RestAssured.port)
                .contentType("application/json")
                .body(objectMapper.writeValueAsString(request))
                .when()
                .post("/api/payments")
                .then()
                .log().all()
                .statusCode(404)
                .body("code", equalTo(String.valueOf(HttpStatus.NOT_FOUND)))
                .body("message", equalTo(ORDER_NOT_FOUND));
        }

        @DisplayName("잔액이 부족할 경우 INSUFFICIENT_BALANCE 예외가 발생한다.")
        @Test
        void createPayment_InsufficientBalance() throws Exception {
            // given
            User user = User.builder()
                .userName("Test User")
                .point(50L) // 잔액 부족
                .build();
            user = userJPARepository.save(user);

            Order order = Order.builder()
                .userId(user.getUserId())
                .orderAmount(100L)
                .paymentAmount(100L)
                .build();
            order = orderJPARepository.save(order);

            Product product = Product.builder()
                .price(100L)
                .productState(ProductState.OUT_OF_STOCK)
                .productName("Test Product")
                .build();

            product = productJPARepository.save(product);

            ProductQuantity productQuantity = ProductQuantity.builder()
                .productId(product.getProductId())
                .quantity(0L)
                .build();

            productQuantity = productQuantityJPARepository.save(productQuantity);

            OrderProduct orderProduct = OrderProduct.builder()
                .orderId(order.getOrderId())
                .productId(product.getProductId())
                .quantity(1L)
                .build();

            orderProduct = orderProductJPARepository.save(orderProduct);

            RequestCreatePaymentDTO request = new RequestCreatePaymentDTO(order.getOrderId());

            // when & then
            given()
                .baseUri(baseUrl + RestAssured.port)
                .contentType("application/json")
                .body(objectMapper.writeValueAsString(request))
                .when()
                .post("/api/payments")
                .then()
                .log().all()
                .statusCode(409)
                .body("code", equalTo(String.valueOf(HttpStatus.CONFLICT)))
                .body("message", equalTo(INSUFFICIENT_BALANCE));

            // 재고 롤백 확인

            order = orderJPARepository.findById(order.getOrderId()).get();

            productQuantity = productQuantityJPARepository.findById(
                productQuantity.getProductQuantityId()).get();

            assertThat(order.getOrderState()).isEqualTo(OrderState.FAILED);
            assertThat(productQuantity.getQuantity()).isEqualTo(1L);


        }

        // 최종 성공 =====================================

        @DisplayName("유효한 요청으로 결제 생성에 성공한다.")
        @Test
        void createPayment_Success() throws Exception {
            // given
            User user = User.builder()
                .userName("Test User")
                .point(200L) // 충분한 잔액
                .build();
            user = userJPARepository.save(user);

            Order order = Order.builder()
                .userId(user.getUserId())
                .orderAmount(100L)
                .paymentAmount(100L)
                .build();
            order = orderJPARepository.save(order);

            RequestCreatePaymentDTO request = new RequestCreatePaymentDTO(order.getOrderId());

            // when & then
            given()
                .baseUri(baseUrl + RestAssured.port)
                .contentType("application/json")
                .body(objectMapper.writeValueAsString(request))
                .when()
                .post("/api/payments")
                .then()
                .log().all()
                .statusCode(200)
                .body("paymentId", equalTo(1)) // Payment ID가 생성됨
                .body("orderId", equalTo(order.getOrderId().intValue()));
        }
    }
}
