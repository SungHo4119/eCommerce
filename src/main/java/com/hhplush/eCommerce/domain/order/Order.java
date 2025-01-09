package com.hhplush.eCommerce.domain.order;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "orders")
public class Order {

    @Id
    @Column(name = "order_id")
    Long orderId;

    @Column(name = "user_id")
    Long userId;

    @Column(name = "user_coupon_id")
    Long userCouponId;

    @Column(name = "order_amount")
    Long orderAmount;

    @Column(name = "discount_amount")
    Long discountAmount;

    @Column(name = "payment_amount")
    Long paymentAmount;

    @Column(name = "order_state")
    OrderState orderState;

    @Column(name = "order_at")
    LocalDateTime orderAt;
}
