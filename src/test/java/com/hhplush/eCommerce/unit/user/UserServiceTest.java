package com.hhplush.eCommerce.unit.user;

import static com.hhplush.eCommerce.common.exception.message.ExceptionMessage.INSUFFICIENT_BALANCE;
import static com.hhplush.eCommerce.common.exception.message.ExceptionMessage.USER_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import com.hhplush.eCommerce.common.exception.custom.InvalidPaymentCancellationException;
import com.hhplush.eCommerce.common.exception.custom.ResourceNotFoundException;
import com.hhplush.eCommerce.domain.IEventPublisher;
import com.hhplush.eCommerce.domain.user.IUserRepository;
import com.hhplush.eCommerce.domain.user.User;
import com.hhplush.eCommerce.domain.user.UserService;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class UserServiceTest {

    @Mock
    private IUserRepository userRepository;

    @Mock
    private IEventPublisher eventPublisher;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Nested
    @DisplayName("UserService 의 getUserByUserId 메서드 테스트")
    class GetUserTests {

        @Test
        @DisplayName("유저 정보가 있다면 유저객체을 반환한다")
        void getUserByUserId_success() {
            // Given
            Long userId = 1L;
            User user = User.builder()
                .userId(userId)
                .userName("Test User")
                .point(100L)
                .build();
            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            doNothing().when(eventPublisher).publishString("1", "1");

            // When
            User result = userService.getUserByUserId(userId);
            // Then
            assertEquals(user, result);

        }

        @Test
        @DisplayName("유저 정보가 없다면 ResourceNotFoundException 예외를 발생한다")
        void UserNotFound_ResourceNotFoundException() {
            // Given
            Long userId = 1L;
            when(userRepository.findById(userId)).thenReturn(Optional.empty());
            doNothing().when(eventPublisher).publishString("1", "1");

            // When & Then
            ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> userService.getUserByUserId(userId));

            // Then
            assertEquals(exception.getMessage(), USER_NOT_FOUND);
        }
    }


    @Nested
    @DisplayName("UserService 의 chargePoint 메서드 테스트")
    class ChargePointTests {

        @Test
        @DisplayName("포인트 충전에 성공하면 유저 객체에 포인트를 추가 한다.")
        void chargePoint_success() {
            // Given
            Long userId = 1L;
            Long userPoint = 100L;
            Long addPoint = 100L;
            User user = User.builder()
                .userId(userId)
                .point(userPoint)
                .build();

            when(userRepository.save(user)).thenReturn(user);
            // When
            User result = userService.chargePoint(user, addPoint);
            // Then
            assertEquals(user.getPoint(), userPoint + addPoint);

        }
    }


    @Nested
    @DisplayName("UserService 의 decreaseUserPoint 메서드 테스트")
    class DecreaseUserPointTests {

        @Test
        @DisplayName("포인트 차감액 보다 유저 포인트가 많다면 유저 포인트를 차감한다")
        void getUserByUserId_success() {
            // Given
            Long userId = 1L;
            Long userPoint = 100L;
            User user = User.builder()
                .userId(userId)
                .point(userPoint)
                .build();
            // When
            userService.decreaseUserPoint(user, userPoint);
            // Then
            assertEquals(user.getPoint(), 0);

        }

        @Test
        @DisplayName("포인트 차감액 보다 유저 포인트가 적다면 InvalidPaymentCancellationException 예외를 발생한다")
        void UserNotFound_ResourceNotFoundException() {
            // Given
            Long userId = 1L;
            Long userPoint = 100L;
            Long decreasePoint = 200L;
            User user = User.builder()
                .userId(userId)
                .point(userPoint)
                .build();

            // When & Then
            InvalidPaymentCancellationException exception = assertThrows(
                InvalidPaymentCancellationException.class,
                () -> userService.decreaseUserPoint(user, decreasePoint));

            // Then
            assertEquals(exception.getMessage(), INSUFFICIENT_BALANCE);
        }
    }
}
