package com.hhplush.eCommerce.integration.business.product;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.hhplush.eCommerce.domain.product.Product;
import com.hhplush.eCommerce.domain.product.ProductState;
import com.hhplush.eCommerce.domain.product.ProductTop;
import com.hhplush.eCommerce.integration.config.IntegrationTest;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

public class ProductServiceTest extends IntegrationTest {

    @Nested
    @DisplayName("상품 목록 조회")
    @Transactional
    class GetProducts {

        @Test
        void 상품목록조회_성공() {
            // given
            Product product = Product.builder()
                .productName("Test Product")
                .price(1000L)
                .build();
            product = productJPARepository.save(product);
            // when
            List<Product> result = productService.getProducts();

            // then
            assertEquals(1, result.size());
            assertEquals(product.getProductName(), result.get(0).getProductName());

        }
    }


    @Nested
    @DisplayName("상위 상품 목록 조회")
    @Transactional
    class GetTopProducts {

        @Test
        void 상품목록조회_성공() {
            // given
            ProductTop product = ProductTop.builder()
                .productName("Test Product")
                .price(1000L)
                .productId(1L)
                .productRank(1L)
                .productState(ProductState.IN_STOCK)
                .totalQuantity(100L)
                .createAt(LocalDate.now())
                .build();

            product = productTopJPARepository.save(product);
            // when
            List<ProductTop> result = productService.getTopProducts();

            // then
            assertEquals(1, result.size());
            assertEquals(product.getProductName(), result.get(0).getProductName());

        }
    }
}
