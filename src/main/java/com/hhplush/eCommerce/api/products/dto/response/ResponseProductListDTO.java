package com.hhplush.eCommerce.api.products.dto.response;

import com.hhplush.eCommerce.domain.product.ProductState;
import lombok.Builder;

@Builder
public record ResponseProductListDTO(
    Long productId,
    String productName,
    Long price,
    ProductState productState
) {

}
