package com.hhplush.eCommerce.domain.product;

import static com.hhplush.eCommerce.common.exception.message.ExceptionMessage.PRODUCT_LIMIT_EXCEEDED;

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
import lombok.Setter;

@Entity
@Table(name = "product_quantity")
@Getter
@Setter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductQuantity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_quantity_id")
    Long productQuantityId;
    @Column(name = "product_id")
    Long productId;
    @Column(name = "quantity")
    Long quantity;

    public void decreaseProductCount(Long quantity) {
        if (this.quantity < quantity) {
            throw new LimitExceededException(PRODUCT_LIMIT_EXCEEDED);
        }
        this.quantity = this.quantity - quantity;
    }

    public void increaseProductCount(Long quantity) {
        this.quantity = this.quantity + quantity;
    }

}
