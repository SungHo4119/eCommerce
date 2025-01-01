package com.hhplush.eCommerce.common.dto.product.response;

import com.hhplush.eCommerce.domain.entitiy.ProductState;
import lombok.Builder;

@Builder
public record ResponseProductListDTO(
    Integer productId,
    String productName,
    Integer price,
    ProductState productState
) {

}
