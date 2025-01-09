package com.hhplush.eCommerce.domain.user;

import static com.hhplush.eCommerce.common.exception.message.ExceptionMessage.INSUFFICIENT_BALANCE;

import com.hhplush.eCommerce.common.exception.custom.InvalidPaymentCancellationException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
@Table(name = "user")
public class User {

    @Id
    @Column(name = "user_id")
    Long userId;

    @Column(name = "user_name")
    String userName;

    @Column(name = "point")
    Long point;


    public void chargePoint(Long point) {
        this.point += point;
    }

    public void decreasePoint(Long point) {
        if (this.point < point) {
            throw new InvalidPaymentCancellationException(INSUFFICIENT_BALANCE);
        }
        this.point -= point;
    }
}
