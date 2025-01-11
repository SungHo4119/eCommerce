package com.hhplush.eCommerce.business.user;

import com.hhplush.eCommerce.common.exception.custom.ResourceNotFoundException;
import com.hhplush.eCommerce.common.exception.message.ExceptionMessage;
import com.hhplush.eCommerce.domain.user.User;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserLoader {

    private final IUserRepository userRepository;


    // 유저 정보 조회
    public User getUserByUserId(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new ResourceNotFoundException(ExceptionMessage.USER_NOT_FOUND);
        }
        return user.get();
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