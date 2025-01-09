package com.hhplush.eCommerce.infrastructure.product;

import com.hhplush.eCommerce.domain.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IProductJPARepository extends JpaRepository<Product, Long> {

}
