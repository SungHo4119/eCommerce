package com.hhplush.eCommerce.unit.user;

import static com.hhplush.eCommerce.common.exception.message.ExceptionMessage.INSUFFICIENT_BALANCE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.hhplush.eCommerce.common.exception.custom.InvalidPaymentCancellationException;
import com.hhplush.eCommerce.domain.user.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class UserTest {

    private User user;

    @Nested
    @DisplayName("유저 도메인 객체 테스트")
    class UserDomainTests {

        @Test
        @DisplayName("유저 포인트 충전시 포인트가 증가한다")
        void chargePoint_success() {
            // Given
            Long point = 100L;
            user = User.builder().point(point).build();

            // When
            user.chargePoint(100L);

            // Then
            assert user.getPoint() == 200L;
        }

        @Test
        @DisplayName("유저 포인트 차감시 포인트가 감소한다")
        void decreasePoint_success() {
            // Given
            Long point = 100L;
            user = User.builder().point(point).build();

            // When
            user.decreasePoint(100L);

            // Then
            assert user.getPoint() == 0L;
        }

        @Test
        @DisplayName("유저 포인트 차감시 포인트가 부족하면 예외가 발생한다")
        void decreasePoint_InvalidPaymentCancellationException() {
            // Given
            Long point = 100L;
            user = User.builder().point(point).build();

            // When
            InvalidPaymentCancellationException exception = assertThrows(
                InvalidPaymentCancellationException.class, () -> user.decreasePoint(200L));
            // Then
            assertEquals(exception.getMessage(), INSUFFICIENT_BALANCE);
        }
    }

}
