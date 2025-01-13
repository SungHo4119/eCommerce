package com.hhplush.eCommerce.unit.coupon;

import static com.hhplush.eCommerce.common.exception.message.ExceptionMessage.COUPON_LIMIT_EXCEEDED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.hhplush.eCommerce.common.exception.custom.LimitExceededException;
import com.hhplush.eCommerce.domain.coupon.CouponQuantity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class CouponQuantityTest {

    private CouponQuantity couponQuantity;

    @Nested
    @DisplayName("쿠폰 수량 도메인 객체 테스트")
    class CouponQuantityDomainTests {

        @Test
        @DisplayName("쿠폰 재고 차감시 차감할 재고가 부족하다면 LimitExceededException 예외가 발생한다")
        void COUPON_LIMIT_EXCEEDED_LimitExceededException() {
            // Given
            Long quantity = 0L;
            couponQuantity = CouponQuantity.builder().quantity(quantity).build();

            // When
            LimitExceededException exception = assertThrows(
                LimitExceededException.class,
                () -> couponQuantity.decreaseCouponCount());
            // Then
            assertEquals(exception.getMessage(), COUPON_LIMIT_EXCEEDED);
        }

        @Test
        @DisplayName("쿠폰 재고 차감시 차감할 재고가 충분하다면 재고가 감소한다")
        void decreaseCouponCount_success() {
            // Given
            Long quantity = 5L;
            couponQuantity = CouponQuantity.builder().quantity(quantity).build();

            // When
            couponQuantity.decreaseCouponCount();
            // Then
            assertEquals(couponQuantity.getQuantity(), 4L);
        }
    }
}
