package com.hhplush.eCommerce.common.dto.product.response;

import com.hhplush.eCommerce.domain.entitiy.ProductState;
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
