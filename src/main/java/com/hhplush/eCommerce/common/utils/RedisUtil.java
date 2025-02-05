package com.hhplush.eCommerce.common.utils;

public class RedisUtil {

    public static final String COUPON_KEY = "coupon:";
    public static final String USER_KEY = "user:";
    public static final String COUPON_QUEUE_KEY = "coupon:queue";
    public static final String COUPON_ISSUED_KEY = "coupon:issued";
    public static final int POLL_SIZE = 10;

    public static String getCouponKey(String couponId) {
        return COUPON_KEY + couponId;
    }

    public static String getUserKey(String userId) {
        return USER_KEY + userId;
    }

    public static String getIssuedCouponKey(String couponId, String userId) {
        return COUPON_KEY + couponId + ":" + USER_KEY + userId;
    }
}
