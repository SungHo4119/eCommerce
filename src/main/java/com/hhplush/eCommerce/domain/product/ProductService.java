package com.hhplush.eCommerce.domain.product;

import static com.hhplush.eCommerce.common.exception.message.ExceptionMessage.PRODUCT_NOT_FOUND;

import com.hhplush.eCommerce.common.exception.custom.ResourceNotFoundException;
import com.hhplush.eCommerce.domain.order.OrderProduct;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class ProductService {

    private final IProductRepository productRepository;

    // 제품 목록 조회
    public List<Product> getProductList() {
        return productRepository.productFindAll();
    }

    // 제품 목록 조회(제품 ID)
    public List<Product> getProductListByProductIds(List<Long> productIds) {
        return productRepository.productFindByIds(productIds);
    }


    // 제품 수량 확인
    public List<ProductQuantity> getProductQuantityListWithLock(List<Long> productIds) {
        List<ProductQuantity> productQuantities = productRepository.productQuantityFindByIdsWithLock(
            productIds);
        if (productQuantities.size() != productIds.size()) {
            throw new ResourceNotFoundException(PRODUCT_NOT_FOUND);
        }
        return productQuantities;
    }

    // OUT_OF_STOCK 인 상품 조회
    public List<Product> findEmptyProducts(List<Product> products) {
        return products.stream()
            .filter(product -> product.getProductState().equals(ProductState.OUT_OF_STOCK))
            .toList();
    }

    // 재고 업데이트 / 상품 상태 업데이트
    public void saveAllProductQuantity(List<ProductQuantity> productQuantities,
        List<Product> emptyProducts) {
        // 재고 업데이트
        productRepository.productQuantitySaveAll(productQuantities);
        if (!emptyProducts.isEmpty()) {
            // 상품 상태 업데이트
            productRepository.productSaveAll(emptyProducts);
        }
    }

    // 재고 복구
    public void cancelProductQuantity(List<ProductQuantity> productQuantities,
        List<OrderProduct> orderProductList) {
        for (ProductQuantity productQuantity : productQuantities) {
            OrderProduct orderProduct = orderProductList.stream()
                .filter(op -> op.getProductId().equals(productQuantity.getProductId()))
                .findFirst().orElseThrow(() -> new ResourceNotFoundException(PRODUCT_NOT_FOUND));

            // 재고 복구
            productQuantity.increaseProductCount(orderProduct.getQuantity());
        }
        productRepository.productQuantitySaveAll(productQuantities);
    }

    // 상위 상품 목록 조회
    public List<ProductTop> getTopProductList(LocalDate toDay) {
        return productRepository.findProductTopByToDay(toDay);
    }

    // 상위 상품 목록 조회 ( 캐시 사용 )
    @Cacheable(value = "productTop", keyGenerator = "localDateKeyGenerator")
    public List<ProductTop> getTopProductListV2(LocalDate toDay) {
        log.info("Fetching productTop from DB for date: {}", toDay);
        return productRepository.findProductTopByToDay(toDay);
    }
}
