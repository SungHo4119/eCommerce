package com.hhplush.eCommerce.common.dto.order.request;

import java.util.List;

public record RequestCreateOrderDTO(
    Integer userId,
    List<Product> product
) {

    public record Product(
        Integer productId,
        Integer price,
        Integer quantity
    ) {

    }
}
