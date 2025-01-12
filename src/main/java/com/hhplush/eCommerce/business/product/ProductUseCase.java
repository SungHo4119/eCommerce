package com.hhplush.eCommerce.business.product;

import com.hhplush.eCommerce.common.utils.DateTimeUtils;
import com.hhplush.eCommerce.domain.product.Product;
import com.hhplush.eCommerce.domain.product.ProductService;
import com.hhplush.eCommerce.domain.product.ProductTop;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductUseCase {


    private final ProductService productService;

    // 상품 목록 조회
    public List<Product> getProducts() {
        return productService.getProductList();
    }

    // 상위 상품 목록 조회
    public List<ProductTop> getTopProducts() {
        return productService.getTopProductList(DateTimeUtils.localDateNow());
    }
}
