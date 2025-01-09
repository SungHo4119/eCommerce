package com.hhplush.eCommerce.infrastructure.product;

import com.hhplush.eCommerce.domain.product.ProductQuantity;
import jakarta.persistence.LockModeType;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

public interface IProductQuantityJPARepository extends JpaRepository<ProductQuantity, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<ProductQuantity> findByProductIdIn(List<Long> productIds);

}
