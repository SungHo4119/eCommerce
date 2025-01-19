package com.hhplush.eCommerce.domain.coupon;

import static com.hhplush.eCommerce.common.exception.message.ExceptionMessage.COUPON_LIMIT_EXCEEDED;

import com.hhplush.eCommerce.common.exception.custom.LimitExceededException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "coupon_quantity")
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CouponQuantity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "coupon_quantity_id")
    Long couponQuantityId;

    @Column(name = "coupon_id")
    Long couponId;

    @Column(name = "quantity")
    Long quantity;

    public void decreaseCouponCount() {
        if (this.quantity <= 0) {
            throw new LimitExceededException(COUPON_LIMIT_EXCEEDED);
        }
        this.quantity = this.quantity - 1;
    }

    public boolean isValidQuantity() {
        return this.quantity <= 0;
    }
}
