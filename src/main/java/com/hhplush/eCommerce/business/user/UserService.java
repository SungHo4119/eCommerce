package com.hhplush.eCommerce.business.user;

import com.hhplush.eCommerce.business.coupon.UserCouponLoader;
import com.hhplush.eCommerce.domain.coupon.UserCouponInfo;
import com.hhplush.eCommerce.domain.user.User;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserLoader userLoader;

    private final UserCouponLoader userCouponLoader;

    // 유저 생성
    public User saveUser(User user) {
        return userLoader.saveUser(user);
    }

    // 유저 조회
    public User getUser(Long userId) {
        return userLoader.getUserByUserId(userId);
    }

    // 유저 포인트 충전
    public User chargeUserPoint(Long userId, Long point) {
        User user = userLoader.getUserByUserId(userId);
        user.chargePoint(point);
        userLoader.saveUser(user);
        return user;
    }

    // 유저가 보유한 쿠폰 조회
    public List<UserCouponInfo> getUserCoupon(Long userId) {
        userLoader.getUserByUserId(userId);
        return userCouponLoader.getUserCouponListByUserId(userId);
    }
}
