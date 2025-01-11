package com.hhplush.eCommerce.api.products.dto.response;

import com.hhplush.eCommerce.domain.product.ProductState;
import java.time.LocalDate;
import lombok.Builder;

@Builder
public record ResponseProductTopDTO(
    Long productTopId,
    Long productId,
    String productName,
    Long price,
    ProductState productState,
    Long product_rank,
    LocalDate createAt

) {

}
