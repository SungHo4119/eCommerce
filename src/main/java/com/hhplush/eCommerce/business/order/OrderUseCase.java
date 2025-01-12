package com.hhplush.eCommerce.business.order;

import com.hhplush.eCommerce.domain.coupon.Coupon;
import com.hhplush.eCommerce.domain.coupon.CouponService;
import com.hhplush.eCommerce.domain.coupon.UserCoupon;
import com.hhplush.eCommerce.domain.order.Order;
import com.hhplush.eCommerce.domain.order.OrderProduct;
import com.hhplush.eCommerce.domain.order.OrderSerivce;
import com.hhplush.eCommerce.domain.product.Product;
import com.hhplush.eCommerce.domain.product.ProductQuantity;
import com.hhplush.eCommerce.domain.product.ProductService;
import com.hhplush.eCommerce.domain.user.UserService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderUseCase {

    private final UserService userService;
    private final CouponService couponService;
    private final ProductService productService;
    private final OrderSerivce orderSerivce;

    // 주문 생성
    public Order createOrder(Long userId, Long userCouponId, List<OrderProduct> orderProductList) {

        // 주문 아이디 추출 (중복 제거하여 생성)
        List<Long> productIds = orderProductList.stream()
            .map(OrderProduct::getProductId)
            .distinct().toList();

        userService.getUserByUserId(userId);
        UserCoupon userCoupon = couponService.getUserCouponByUserCouponId(userCouponId);
        // 쿠폰 사용 여부 확인
        couponService.CheckUserCouponIsUsed(userCoupon);

        Coupon coupon = couponService.getCouponByCouponId(userCoupon.getCoupon().getCouponId());
        // 제품 리스트 조회
        List<Product> productList = productService.getProductListByProductIds(productIds);

        // 주문 상품 체크
        orderSerivce.checkOrderProductList(orderProductList, productList);

        // 상품 재고 조회 ( 락 사용 )
        List<ProductQuantity> productQuantityList = productService.getProductQuantityListWithLock(
            productIds);

        // 주문 시 상품 수량 체크 및 재고 변경
        orderSerivce.validateProductQuantitie(orderProductList, productQuantityList, productList);

        // 금액 계산
        Long orderAmount = orderSerivce.calculateOrderAmount(orderProductList, productList);

        // 주문 생성
        Order order = orderSerivce.createOrder(userId, userCouponId, orderAmount,
            coupon.getDiscountAmount());

        // 재고 업데이트 / 상품 상태 업데이트
        productService.saveAllProductQuantity(productQuantityList, productList);

        // 쿠폰 사용 처리
        couponService.useUserCoupon(userCoupon, true);

        // 주문 상품 저장
        orderSerivce.createOrderProduct(order.getOrderId(), orderProductList);
        return order;

    }
}
