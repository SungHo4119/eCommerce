package com.hhplush.eCommerce.domain.user;

import com.hhplush.eCommerce.common.exception.custom.ResourceNotFoundException;
import com.hhplush.eCommerce.common.exception.message.ExceptionMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserService {

    private final IUserRepository userRepository;


    // 유저 정보 조회
    public User getUserByUserId(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException(ExceptionMessage.USER_NOT_FOUND));
    }

    // 유저 정보 조회(락을 걸며 조회)
    public User getUserByUserIdLock(Long userId) {
        return userRepository.findByIdLock(userId)
            .orElseThrow(() -> new ResourceNotFoundException(ExceptionMessage.USER_NOT_FOUND));
    }

    public User chargePoint(User user, Long point) {
        user.chargePoint(point);
        userRepository.save(user);
        return user;
    }

    // 유저 생성
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    // 유저 포인트 차감
    public void decreaseUserPoint(User user, Long point) {
        user.decreasePoint(point);
        userRepository.save(user);
    }
}