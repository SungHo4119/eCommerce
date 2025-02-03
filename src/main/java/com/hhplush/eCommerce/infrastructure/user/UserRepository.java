package com.hhplush.eCommerce.infrastructure.user;

import com.hhplush.eCommerce.domain.user.IUserRepository;
import com.hhplush.eCommerce.domain.user.User;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserRepository implements IUserRepository {

    private final IUserJPARepository userJPARepository;


    @Override
    public Optional<User> findByIdLock(Long id) {
        return userJPARepository.findByIdWithLock(id);
    }


    @Override
    public Optional<User> findById(Long id) {
        return userJPARepository.findById(id);
    }

    @Override
    public User save(User user) {
        return userJPARepository.save(user);
    }

}
