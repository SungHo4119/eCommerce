package com.hhplush.eCommerce.api.products.dto.response;

import com.hhplush.eCommerce.domain.enums.ProductState;
import lombok.Builder;

@Builder
public record ResponseProductListDTO(
    Integer productId,
    String productName,
    Integer price,
    ProductState productState
) {

}
