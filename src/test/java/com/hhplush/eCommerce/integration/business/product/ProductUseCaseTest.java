package com.hhplush.eCommerce.integration.business.product;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import com.hhplush.eCommerce.domain.product.ProductState;
import com.hhplush.eCommerce.domain.product.ProductTop;
import com.hhplush.eCommerce.integration.config.IntegrationTest;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class ProductUseCaseTest extends IntegrationTest {


    @Nested
    @DisplayName("상위 품목 목록 조회")
    class GetTopProducts {

        @Test
        public void 상위상품여러번조회시_캐시를_이용하여_조회해야한다() {
            // given
            LocalDate today = LocalDate.now();
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

            // when
            productUseCase.getTopProductsV2();

            productUseCase.getTopProductsV2();

            List<ProductTop> result = productUseCase.getTopProductsV2();

            // then
            assertThat(result).hasSize(2);
        }

    }

}
