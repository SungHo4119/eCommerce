package com.hhplush.eCommerce.business.product;

import com.hhplush.eCommerce.domain.product.Product;
import com.hhplush.eCommerce.domain.product.ProductQuantity;
import com.hhplush.eCommerce.domain.product.ProductTop;
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
