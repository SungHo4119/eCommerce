package com.hhplush.eCommerce.infrastructure.user;

import com.hhplush.eCommerce.business.user.IUserRepository;
import com.hhplush.eCommerce.domain.coupon.UserCouponInfo;
import com.hhplush.eCommerce.domain.user.User;
import com.hhplush.eCommerce.infrastructure.coupon.IUserCouponJPARepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserRepository implements IUserRepository {

    private final IUserJPARepository userJPARepository;
    private final IUserCouponJPARepository userCouponJPARepository;


    @Override
    public Optional<User> findById(Long id) {
        return userJPARepository.findById(id);
    }

    @Override
    public User save(User user) {
        return userJPARepository.save(user);
    }

    @Override
    public List<UserCouponInfo> findUserCouponByUserId(Long userId) {
        return userCouponJPARepository.findCouponsByUserId(userId);
    }
}
