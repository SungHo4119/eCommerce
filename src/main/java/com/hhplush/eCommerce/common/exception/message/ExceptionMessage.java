package com.hhplush.eCommerce.common.exception.message;


public class ExceptionMessage {

    // TODO: Request Error Messages ( BadRequestException )

    // TODO: Not Found Error Messages ( ResourceNotFoundException )
    public static final String USER_NOT_FOUND = "사용자를 찾을 수 없습니다.";
    public static final String PRODUCT_NOT_FOUND = "제품을 찾을 수 없습니다.";
    public static final String ORDER_NOT_FOUND = "주문을 찾을 수 없습니다.";
    public static final String COUPON_NOT_FOUND = "쿠폰을 찾을 수 없습니다.";

    // TODO: Already Exists Error Messages ( AlreadyExistsException )
    public static final String COUPON_ALREADY_EXISTS = "이미 보유한 쿠폰입니다.";
    public static final String COUPON_USE_ALREADY_EXISTS = "이미 사용한 쿠폰입니다.";
    // TODO: Limit Exceeded Error Messages ( LimitExceededException )

    public static final String POINT_CHARGE_LIMIT_EXCEEDED = "포인트 충전 한도를 초과하였습니다.";
    public static final String POINT_SAVE_LIMIT_EXCEEDED = "포인트 보유 한도를 초과하였습니다.";
    public static final String COUPON_LIMIT_EXCEEDED = "쿠폰 발급 한도를 초과하였습니다.";
    public static final String PRODUCT_LIMIT_EXCEEDED = "제품 구매 한도를 초과하였습니다.";

    // TODO: Else ( ConflictException )
    public static final String INSUFFICIENT_BALANCE = "잔액이 부족합니다.";
}