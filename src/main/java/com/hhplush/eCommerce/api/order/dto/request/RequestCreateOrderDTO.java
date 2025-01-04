package com.hhplush.eCommerce.api.order.dto.request;

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
