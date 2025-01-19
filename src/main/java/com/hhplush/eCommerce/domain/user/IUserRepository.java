package com.hhplush.eCommerce.domain.user;

import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public interface IUserRepository {

    Optional<User> findById(Long userId);

    User save(User user);

}
