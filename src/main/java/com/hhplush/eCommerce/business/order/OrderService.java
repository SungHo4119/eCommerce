package com.hhplush.eCommerce.business.order;

import static com.hhplush.eCommerce.common.exception.message.ExceptionMessage.COUPON_USE_ALREADY_EXISTS;
import static com.hhplush.eCommerce.common.exception.message.ExceptionMessage.PRODUCT_NOT_FOUND;

import com.hhplush.eCommerce.business.coupon.CouponLoader;
import com.hhplush.eCommerce.business.coupon.UserCouponLoader;
import com.hhplush.eCommerce.business.product.ProductLoader;
import com.hhplush.eCommerce.business.user.UserLoader;
import com.hhplush.eCommerce.common.exception.custom.ResourceNotFoundException;
import com.hhplush.eCommerce.domain.coupon.Coupon;
import com.hhplush.eCommerce.domain.coupon.UserCoupon;
import com.hhplush.eCommerce.domain.order.Order;
import com.hhplush.eCommerce.domain.order.OrderProduct;
import com.hhplush.eCommerce.domain.product.Product;
import com.hhplush.eCommerce.domain.product.ProductQuantity;
import com.hhplush.eCommerce.domain.product.ProductState;
import com.hhplush.eCommerce.domain.user.User;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {

    private final UserLoader userLoader;
    private final CouponLoader couponLoader;
    private final UserCouponLoader userCouponLoader;
    private final ProductLoader productLoader;
    private final OrderLoader orderLoader;

    // 주문 생성
    public Order createOrder(Long userId, Long userCouponId, List<OrderProduct> orderProductList,
        List<Long> productIds) {

        //--------------------------------------------
        // 여기부터 데이터 조회
        //--------------------------------------------
        // 유저 조회
        User user = userLoader.getUserByUserId(userId);

        // 사용자 쿠폰 조회
        UserCoupon userCoupon = userCouponLoader.getUserCouponByUserCouponId(userCouponId);

        // 쿠폰 사용 여부 확인
        if (userCoupon.getCouponUse()) {
            throw new ResourceNotFoundException(COUPON_USE_ALREADY_EXISTS);
        }

        // 쿠폰 정보 조회 ( 할인 금액  확인 )
        Coupon coupon = couponLoader.getCouponByCouponId(userCoupon.getCouponId());

        // 제품 조회
        List<Product> productList = productLoader.checkGetProductList(
            orderProductList.stream()
                .map(OrderProduct::getProductId).toList());

        // 제품 갯수 비교
        if (productList.size() != orderProductList.size()) {
            throw new ResourceNotFoundException(PRODUCT_NOT_FOUND);
        }
        // 재고 확인
        List<ProductQuantity> productQuantityList = productLoader.getProductQuantityListWithLock(
            productIds);

        //--------------------------------------------
        // 여기부터 메인 로직
        //--------------------------------------------

        // 재고 검증 및 차감
        productLoader.validateProductQuantitie(orderProductList,
            productQuantityList);

        // 재고가 0인 상품 상태 변경
        for (ProductQuantity productQuantity : productQuantityList) {
            if (productQuantity.getQuantity() == 0) {
                productList.stream()
                    .filter(
                        product -> product.getProductId().equals(productQuantity.getProductId()))
                    .findFirst()
                    .ifPresent(product -> product.setProductState(ProductState.OUT_OF_STOCK));
            }
        }

        // 금액 계산
        Long orderAmount = productList.stream()
            .map(product -> product.getPrice() * orderProductList.stream()
                .filter(orderProduct -> orderProduct.getProductId().equals(product.getProductId()))
                .findFirst().get().getQuantity())
            .reduce(0L, Long::sum);
        // 할인금액
        Long discountAmount = coupon.getDiscountAmount();
        // 총액
        Long paymentAmount = (orderAmount - discountAmount) < 0 ? 0 : orderAmount - discountAmount;

        // 주문 생성
        Order order = orderLoader.createOrder(userId, userCouponId, orderAmount, discountAmount,
            paymentAmount);

        // 재고 업데이트 / 상품 상태 업데이트
        productLoader.saveAllProductQuantity(productQuantityList, productList);

        // 쿠폰 사용 처리
        userCouponLoader.useUserCoupon(userCoupon, true);

        // 주문 상품 저장
        orderLoader.createOrderProduct(order.getOrderId(), orderProductList);
        return order;

    }
}
