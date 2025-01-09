package com.hhplush.eCommerce.domain.product;

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
@Table(name = "coupons")
public class ProductQuantity {

    @Id
    @Column(name = "product_quantity_id")
    Long productQuantityId;
    @Column(name = "product_id")
    Long productId;
    @Column(name = "quantity")
    Long quantity;

    public void decreaseProductCount(Long quantity) {
        this.quantity = this.quantity - quantity;
    }

    public void increaseProductCount(Long quantity) {
        this.quantity = this.quantity + quantity;
    }

}
