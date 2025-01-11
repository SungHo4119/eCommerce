package com.hhplush.eCommerce.business.product;

import static com.hhplush.eCommerce.common.exception.message.ExceptionMessage.PRODUCT_LIMIT_EXCEEDED;
import static com.hhplush.eCommerce.common.exception.message.ExceptionMessage.PRODUCT_NOT_FOUND;

import com.hhplush.eCommerce.common.exception.custom.LimitExceededException;
import com.hhplush.eCommerce.common.exception.custom.ResourceNotFoundException;
import com.hhplush.eCommerce.domain.order.OrderProduct;
import com.hhplush.eCommerce.domain.product.Product;
import com.hhplush.eCommerce.domain.product.ProductQuantity;
import com.hhplush.eCommerce.domain.product.ProductState;
import com.hhplush.eCommerce.domain.product.ProductTop;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductLoader {

    private final IProductRepository productRepository;

    // 제품 목록 조회
    public List<Product> getProductList() {
        return productRepository.productFindAll();
    }

    // 제품 목록 확인
    public List<Product> checkGetProductList(List<Long> productIds) {
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

    // 재고 확인 및 검증
    public List<ProductQuantity> validateProductQuantitie(List<OrderProduct> orderProductList,
        List<ProductQuantity> productQuantityList) {
        // 주문 가능한지 수량 확인
        for (ProductQuantity productQuantity : productQuantityList) {
            OrderProduct orderProduct = orderProductList.stream()
                .filter(op -> op.getProductId().equals(productQuantity.getProductId()))
                .findFirst().orElseThrow(() -> new ResourceNotFoundException(PRODUCT_NOT_FOUND));

            // 재고 수 확인
            if (orderProduct.getQuantity() > productQuantity.getQuantity()) {
                throw new LimitExceededException(PRODUCT_LIMIT_EXCEEDED);
            }
            // 재고 차감
            productQuantity.decreaseProductCount(orderProduct.getQuantity());
        }
        return productQuantityList;
    }

    // 재고 업데이트 / 상품 상태 업데이트
    public void saveAllProductQuantity(List<ProductQuantity> productQuantities,
        List<Product> products) {
        // 재고 업데이트
        productRepository.productQuantitySaveAll(productQuantities);

        // 상품 상태 업데이트
        List<Product> emptyProducts = products.stream()
            .filter(product -> product.getProductState().equals(ProductState.OUT_OF_STOCK))
            .toList();
        productRepository.productSaveAll(emptyProducts);

    }

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
}
