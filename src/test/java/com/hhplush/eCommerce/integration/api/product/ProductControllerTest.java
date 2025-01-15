package com.hhplush.eCommerce.integration.api.product;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

import com.hhplush.eCommerce.domain.product.Product;
import com.hhplush.eCommerce.domain.product.ProductState;
import com.hhplush.eCommerce.domain.product.ProductTop;
import com.hhplush.eCommerce.integration.config.IntegrationTest;
import io.restassured.RestAssured;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class ProductControllerTest extends IntegrationTest {

    @Nested
    @DisplayName("[GET] /api/products 상품 목록 조회")
    class ListProducts {

        @DisplayName("상품 목록이 비어있는 경우 빈 배열을 반환한다.")
        @Test
        void listProducts_emptyList() {
            // when & then
            given()
                .baseUri(baseUrl + RestAssured.port)
                .when()
                .get("/api/products")
                .then()
                .statusCode(200)
                .body("$", hasSize(0));
        }

        @DisplayName("상품 목록을 성공적으로 조회한다.")
        @Test
        void listProducts_success() {
            // given
            Product product1 = Product.builder()
                .productName("Product 1")
                .price(1000L)
                .productState(ProductState.IN_STOCK)
                .build();
            Product product2 = Product.builder()
                .productName("Product 2")
                .price(2000L)
                .productState(ProductState.OUT_OF_STOCK)
                .build();
            productJPARepository.save(product1);
            productJPARepository.save(product2);

            // when & then
            given()
                .baseUri(baseUrl + RestAssured.port)
                .when()
                .get("/api/products")
                .then()
                .statusCode(200)
                .body("$", hasSize(2))
                .body("[0].productId", equalTo(product1.getProductId().intValue()))
                .body("[0].productName", equalTo(product1.getProductName()))
                .body("[0].price", equalTo(product1.getPrice().intValue()))
                .body("[0].productState", equalTo(product1.getProductState().name()))
                .body("[1].productId", equalTo(product2.getProductId().intValue()))
                .body("[1].productName", equalTo(product2.getProductName()))
                .body("[1].price", equalTo(product2.getPrice().intValue()))
                .body("[1].productState", equalTo(product2.getProductState().name()));
        }
    }

    @Nested
    @DisplayName("[GET] /api/products/top 상위 상품 목록 조회")
    class TopProducts {

        @DisplayName("상위 상품 목록이 비어있는 경우 빈 배열을 반환한다.")
        @Test
        void topProducts_emptyList() {
            // when & then
            given()
                .baseUri(baseUrl + RestAssured.port)
                .when()
                .get("/api/products/top")
                .then()
                .statusCode(200)
                .body("$", hasSize(0));
        }

        @DisplayName("상위 상품 목록을 성공적으로 조회한다.")
        @Test
        void topProducts_success() {
            // given
            ProductTop productTop1 = ProductTop.builder()
                .productId(1L)
                .productName("Top Product 1")
                .price(1500L)
                .productState(ProductState.IN_STOCK)
                .productRank(1L)
                .totalQuantity(100L)
                .createAt(LocalDate.now())
                .build();
            ProductTop productTop2 = ProductTop.builder()
                .productId(2L)
                .productName("Top Product 2")
                .price(2500L)
                .productState(ProductState.IN_STOCK)
                .productRank(2L)
                .totalQuantity(200L)
                .createAt(LocalDate.now())
                .build();
            productTopJPARepository.save(productTop1);
            productTopJPARepository.save(productTop2);

            // when & then
            given()
                .baseUri(baseUrl + RestAssured.port)
                .when()
                .get("/api/products/top")
                .then()
                .statusCode(200)
                .body("$", hasSize(2))
                .body("[0].productTopId", equalTo(productTop1.getProductTopId().intValue()))
                .body("[0].productId", equalTo(productTop1.getProductId().intValue()))
                .body("[0].productName", equalTo(productTop1.getProductName()))
                .body("[0].price", equalTo(productTop1.getPrice().intValue()))
                .body("[0].productState", equalTo(productTop1.getProductState().name()))
                .body("[0].product_rank", equalTo(productTop1.getProductRank().intValue()))
                .body("[1].productTopId", equalTo(productTop2.getProductTopId().intValue()))
                .body("[1].productId", equalTo(productTop2.getProductId().intValue()))
                .body("[1].productName", equalTo(productTop2.getProductName()))
                .body("[1].price", equalTo(productTop2.getPrice().intValue()))
                .body("[1].productState", equalTo(productTop2.getProductState().name()))
                .body("[1].product_rank", equalTo(productTop2.getProductRank().intValue()));
        }
    }
}
