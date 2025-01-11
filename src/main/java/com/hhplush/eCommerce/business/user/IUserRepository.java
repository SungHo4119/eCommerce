package com.hhplush.eCommerce.business.user;

import com.hhplush.eCommerce.domain.coupon.UserCouponInfo;
import com.hhplush.eCommerce.domain.user.User;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public interface IUserRepository {

    List<UserCouponInfo> findUserCouponByUserId(Long userId);

    Optional<User> findById(Long userId);

    User save(User user);

}
