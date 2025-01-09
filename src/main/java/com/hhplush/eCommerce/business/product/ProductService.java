package com.hhplush.eCommerce.business.product;

import com.hhplush.eCommerce.domain.product.Product;
import com.hhplush.eCommerce.domain.product.ProductTop;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductService {


    private final ProductLoader productLoader;

    // 상품 목록 조회
    public List<Product> getProducts() {
        return productLoader.getProductList();
    }

    // 상위 상품 목록 조회
    public List<ProductTop> getTopProducts() {
        LocalDate toDay = LocalDate.now();
        return productLoader.getTopProductList(toDay);
    }
}
