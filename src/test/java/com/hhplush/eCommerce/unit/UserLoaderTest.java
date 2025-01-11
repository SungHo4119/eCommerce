package com.hhplush.eCommerce.unit;

import static com.hhplush.eCommerce.common.exception.message.ExceptionMessage.INSUFFICIENT_BALANCE;
import static com.hhplush.eCommerce.common.exception.message.ExceptionMessage.USER_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.hhplush.eCommerce.business.user.IUserRepository;
import com.hhplush.eCommerce.business.user.UserLoader;
import com.hhplush.eCommerce.common.exception.custom.InvalidPaymentCancellationException;
import com.hhplush.eCommerce.common.exception.custom.ResourceNotFoundException;
import com.hhplush.eCommerce.domain.user.User;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class UserLoaderTest {

    @Mock
    private IUserRepository userRepository;

    @InjectMocks
    private UserLoader userLoader;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Nested
    @DisplayName("UserLoader 의 getUserByUserId 메서드 테스트")
    class GetUserTests {

        @Test
        void getUserByUserId_성공() {
            // Given
            Long userId = 1L;
            User user = User.builder()
                .userId(userId)
                .userName("Test User")
                .point(100L)
                .build();
            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            // When
            User result = userLoader.getUserByUserId(userId);
            // Then
            assertEquals(user, result);

        }

        @Test
        void UserNotFound_ResourceNotFoundException() {
            // Given
            Long userId = 1L;
            when(userRepository.findById(userId)).thenReturn(Optional.empty());

            // When & Then
            ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> userLoader.getUserByUserId(userId));

            assertEquals(USER_NOT_FOUND, exception.getMessage());
        }
    }

    @Nested
    @DisplayName("UserLoader 의 decreaseUserPoint 메서드 테스트")
    class DecreaseUserPointTests {

        @Test
        void decreaseUserPoint_성공() {
            // Given
            Long userId = 1L;

            Long userPoint = 100L;
            Long decreasePoint = 100L;
            User user = User.builder()
                .userId(userId)
                .userName("Test User")
                .point(userPoint)
                .build();
            when(userRepository.save(user)).thenReturn(user);
            // When
            userLoader.decreaseUserPoint(user, decreasePoint);
            // Then
            assertEquals(user.getPoint(), userPoint - decreasePoint);
        }

        @Test
        void INSUFFICIENT_BALANCE_InvalidPaymentCancellationException() {
            // Given
            Long userId = 1L;

            Long userPoint = 100L;
            Long decreasePoint = 200L;
            User user = User.builder()
                .userId(userId)
                .userName("Test User")
                .point(userPoint)
                .build();
            when(userRepository.save(user)).thenReturn(user);
            // When // Then
            InvalidPaymentCancellationException exception = assertThrows(
                InvalidPaymentCancellationException.class,
                () -> userLoader.decreaseUserPoint(user, decreasePoint));

            assertEquals(INSUFFICIENT_BALANCE, exception.getMessage());
        }
    }
}
