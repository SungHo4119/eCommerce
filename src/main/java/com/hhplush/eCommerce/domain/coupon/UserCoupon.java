package com.hhplush.eCommerce.domain.coupon;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user_coupon",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"coupon_id", "user_id"})
    })
@Setter
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserCoupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_coupon_id")
    Long userCouponId;
    @Column(name = "user_id", nullable = false)
    Long userId;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id", referencedColumnName = "coupon_id", insertable = false, updatable = false)
    Coupon coupon;

    @Column(name = "coupon_id", nullable = false)
    Long couponId;

    @Column(name = "coupon_use")
    Boolean couponUse;
    @Column(name = "use_at")
    LocalDateTime useAt;
    @Column(name = "create_at")
    LocalDateTime createAt;

}
