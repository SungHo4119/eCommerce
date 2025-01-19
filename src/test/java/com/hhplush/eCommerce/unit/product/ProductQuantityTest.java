package com.hhplush.eCommerce.unit.product;

import static com.hhplush.eCommerce.common.exception.message.ExceptionMessage.PRODUCT_LIMIT_EXCEEDED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.hhplush.eCommerce.common.exception.custom.LimitExceededException;
import com.hhplush.eCommerce.domain.product.ProductQuantity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class ProductQuantityTest {

    private ProductQuantity productQuantity;

    @Nested
    @DisplayName("제품 수량 도메인 객체 테스트")
    class ProductQuantityDomainTests {

        @Test
        @DisplayName("제품 재고 차감시 재고가 감소한다")
        void decreaseQuantity_success() {
            // Given
            Long quantity = 100L;
            productQuantity = ProductQuantity.builder().quantity(quantity).build();

            // When
            productQuantity.decreaseProductCount(quantity);

            // Then
            assertEquals(productQuantity.getQuantity(), 0L);
        }

        @Test
        @DisplayName("제품 재고 차감시 재고가 부족하면 LimitExceededException 예외가 발생한다")
        void decreaseQuantity_LimitExceededException() {
            // Given
            Long quantity = 100L;
            Long quantityDecrease = 200L;
            productQuantity = ProductQuantity.builder().quantity(quantity).build();

            // When
            LimitExceededException exception = assertThrows(
                LimitExceededException.class,
                () -> productQuantity.decreaseProductCount(quantityDecrease));
            // Then
            assertEquals(exception.getMessage(), PRODUCT_LIMIT_EXCEEDED);
        }

        @Test
        @DisplayName("제품 재고 증가시 재고가 증가한다")
        void increaseQuantity_success() {
            // Given
            Long quantity = 100L;
            productQuantity = ProductQuantity.builder().quantity(quantity).build();

            // When
            productQuantity.increaseProductCount(quantity);

            // Then
            assertEquals(productQuantity.getQuantity(), 200L);
        }
    }
}
