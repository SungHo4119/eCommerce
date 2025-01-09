package com.hhplush.eCommerce.domain.coupon;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "coupons")
public class Coupon {

    @Id
    @Column(name = "coupon_id")
    Long couponId;

    @Column(name = "coupon_name")
    String couponName;

    @Column(name = "discount_amount")
    Long discountAmount;

    @Column(name = "coupon_state")
    CouponState couponState;
}
