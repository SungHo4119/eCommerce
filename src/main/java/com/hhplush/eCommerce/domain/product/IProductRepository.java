package com.hhplush.eCommerce.domain.product;

import java.time.LocalDate;
import java.util.List;

public interface IProductRepository {

    List<Product> productFindAll();

    List<Product> productFindByIds(List<Long> productIds);

    List<ProductQuantity> productQuantityFindByIdsWithLock(List<Long> productIds);

    void productQuantitySaveAll(List<ProductQuantity> productQuantities);

    void productSaveAll(List<Product> products);


    List<ProductTop> findProductTopByToDay(LocalDate toDay);
}
