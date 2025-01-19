package com.hhplush.eCommerce.infrastructure.product;

import com.hhplush.eCommerce.domain.product.IProductRepository;
import com.hhplush.eCommerce.domain.product.Product;
import com.hhplush.eCommerce.domain.product.ProductQuantity;
import com.hhplush.eCommerce.domain.product.ProductTop;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProductRepository implements IProductRepository {

    private final IProductJPARepository productJPARepository;
    private final IProductQuantityJPARepository productQuantityJPARepository;

    private final IProductTopJPARepository productTopJPARepository;

    // 상품 목록 조회
    @Override
    public List<Product> productFindAll() {
        return productJPARepository.findAll();
    }

    // 상품 목록 조회 by Ids
    @Override
    public List<Product> productFindByIds(List<Long> productIds) {
        return productJPARepository.findAllById(productIds);
    }

    // 상품 수량 조회 bs Ids
    @Override
    public List<ProductQuantity> productQuantityFindByIdsWithLock(List<Long> productIds) {
        return productQuantityJPARepository.findByProductIdIn(productIds);
    }

    // 재고 업데이트
    @Override
    public void productQuantitySaveAll(List<ProductQuantity> productQuantities) {
        productQuantityJPARepository.saveAll(productQuantities);
    }

    // 상품 저장
    @Override
    public void productSaveAll(List<Product> products) {
        productJPARepository.saveAll(products);
    }

    @Override
    public List<ProductTop> findProductTopByToDay(LocalDate toDay) {
        return productTopJPARepository.findByCreateAt(toDay);
    }
}
