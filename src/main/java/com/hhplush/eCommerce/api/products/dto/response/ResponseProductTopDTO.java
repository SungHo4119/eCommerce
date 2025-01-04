package com.hhplush.eCommerce.api.products.dto.response;

import com.hhplush.eCommerce.domain.enums.ProductState;
import java.time.LocalDateTime;

public record ResponseProductTopDTO(
    Integer productTopId,
    Integer productId,
    String productName,
    Integer price,
    ProductState productState,
    Integer rank,
    LocalDateTime createdAt

) {

}
