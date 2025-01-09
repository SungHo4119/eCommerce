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
@Setter
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "coupons")
public class Product {

    @Id
    @Column(name = "product_id")
    Long productId;
    @Column(name = "product_name")
    String productName;
    @Column(name = "price")
    Long price;
    @Column(name = "product_state")
    ProductState productState;
}
