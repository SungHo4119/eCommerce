package com.hhplush.eCommerce.domain.order;

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
@Table(name = "order_product")
public class OrderProduct {

    @Id
    @Column(name = "order_product_id")
    Long orderProductId;
    @Column(name = "order_id")
    Long orderId;
    @Column(name = "product_id")
    Long productId;
    @Column(name = "quantity")
    Long quantity;
}
