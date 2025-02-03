package com.hhplush.eCommerce.business.user;

import com.hhplush.eCommerce.domain.coupon.CouponService;
import com.hhplush.eCommerce.domain.coupon.UserCoupon;
import com.hhplush.eCommerce.domain.user.User;
import com.hhplush.eCommerce.domain.user.UserService;
import com.hhplush.eCommerce.infrastructure.redis.DistributedLock;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserUseCase {

    private final UserService userService;
    private final CouponService couponService;

    // 유저 생성
    public User saveUser(User user) {
        return userService.saveUser(user);
    }

    // 유저 조회
    public User getUser(Long userId) {
        return userService.getUserByUserId(userId);
    }

    // 유저 포인트 충전
    public User chargeUserPoint(Long userId, Long point) {
        User user = userService.getUserByUserIdLock(userId);
        userService.chargePoint(user, point);
        return user;
    }

    // 유저 포인트 충전(레디스 사용)
    @DistributedLock(key = "userId")
    public User chargeUserPointWithRedis(Long userId, Long point) {
        // 분산락 제거
        User user = userService.getUserByUserId(userId);
        userService.chargePoint(user, point);
        return user;
    }

    // 유저가 보유한 쿠폰 조회
    public List<UserCoupon> getUserCoupon(Long userId) {
        userService.getUserByUserId(userId);
        return couponService.getUserCouponListByUserId(userId);
    }
}
