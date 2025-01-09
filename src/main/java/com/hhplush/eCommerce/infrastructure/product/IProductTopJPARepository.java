package com.hhplush.eCommerce.infrastructure.product;

import com.hhplush.eCommerce.domain.product.ProductTop;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface IProductTopJPARepository extends JpaRepository<ProductTop, Long> {

    @Query("SELECT p FROM ProductTop p WHERE FUNCTION('DATE', p.createAt) = :date")
    List<ProductTop> findByCreateAt(@Param("date") LocalDate date);

}
