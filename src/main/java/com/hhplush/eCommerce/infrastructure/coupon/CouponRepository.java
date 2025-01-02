package com.hhplush.eCommerce.infrastructure.coupon;

import com.hhplush.eCommerce.business.coupon.ICouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CouponRepository implements ICouponRepository {

    private final ICouponJPARepository couponJPARepository;

}
