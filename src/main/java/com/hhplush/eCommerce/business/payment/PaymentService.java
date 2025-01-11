package com.hhplush.eCommerce.business.payment;

import com.hhplush.eCommerce.business.dataCenter.IDataCenter;
import com.hhplush.eCommerce.business.order.OrderLoader;
import com.hhplush.eCommerce.business.product.ProductLoader;
import com.hhplush.eCommerce.business.user.UserLoader;
import com.hhplush.eCommerce.common.exception.custom.InvalidPaymentCancellationException;
import com.hhplush.eCommerce.domain.order.Order;
import com.hhplush.eCommerce.domain.order.OrderProduct;
import com.hhplush.eCommerce.domain.payment.Payment;
import com.hhplush.eCommerce.domain.product.ProductQuantity;
import com.hhplush.eCommerce.domain.user.User;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(noRollbackFor = InvalidPaymentCancellationException.class)
@RequiredArgsConstructor
public class PaymentService {

    private final OrderLoader orderLoader;
    private final UserLoader userLoader;
    private final PaymentLoader paymentLoader;
    private final ProductLoader productLoader;

    private final IDataCenter dataCenter;

    public Payment processPayment(Long orderId) {
        // 주문 정보 조회
        Order order = orderLoader.getOrderByOrderId(orderId);

        // 유저 정보 조회
        User user = userLoader.getUserByUserId(order.getUserId());

        // 결재
        try {
            // 잔액 차감
            userLoader.decreaseUserPoint(user, order.getPaymentAmount());

            // 결재 생성
            Payment payment = paymentLoader.createPayment(order);

            // 데이터 센터 전송
            dataCenter.sendDataCenter();
            return payment;
        } catch (InvalidPaymentCancellationException e) {
            // 주문 취소
            orderLoader.cancelOrder(order);

            // 주문 상품 조회
            List<OrderProduct> orderProductList = orderLoader.getOrderProductByOrderId(orderId);

            // 상품 재고 조회
            List<ProductQuantity> productQuantityList = productLoader.getProductQuantityListWithLock(
                orderProductList.stream().map(OrderProduct::getProductId).toList());

            // 상품 재고 복구
            productLoader.cancelProductQuantity(productQuantityList, orderProductList);

            // 결재 취소
            throw e;
        }
    }
}
