package com.hhplush.eCommerce.unit.product;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.hhplush.eCommerce.domain.product.IProductRepository;
import com.hhplush.eCommerce.domain.product.Product;
import com.hhplush.eCommerce.domain.product.ProductService;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class ProductServiceTest {

    @Mock
    private IProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Nested
    @DisplayName("ProductService 의 getProductList 메서드 테스트")
    class GetProductListTests {

        @DisplayName("제품이 여러개 존재할 경우 제품 리스트를 반환한다.")
        @Test
        void getProductList_success_productList() {
            // given
            List<Product> productList = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                productList.add(Product.builder().build());
            }
            when(productRepository.productFindAll()).thenReturn(productList);

            // when
            List<Product> result = productService.getProductList();

            // then
            assertEquals(productList, result);
        }

        @DisplayName("제품이 존재하지 않을 경우 빈 리스트를 반환한다.")
        @Test
        void getProductList_success_emptyList() {
            // given
            List<Product> productList = new ArrayList<>();
            when(productRepository.productFindAll()).thenReturn(productList);

            // when
            List<Product> result = productService.getProductList();

            // then
            assertEquals(productList, result);
        }
    }

    @Nested
    @DisplayName("ProductService 의 checkGetProductList 메서드 테스트")
    class CheckGetProductList {

        @DisplayName("제품이 여러개 존재할 경우 제품 리스트를 반환한다.")
        @Test
        void checkGetProductList_success_productList() {
            // given
            List<Product> productList = new ArrayList<>();
            List<Long> productIdList = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                productList.add(Product.builder().productId((long) i).build());
                productIdList.add((long) i);
            }
            when(productRepository.productFindByIds(productIdList)).thenReturn(productList);

            // when
            List<Product> result = productService.getProductListByProductIds(productIdList);

            // then
            assertEquals(productList, result);
        }

        @DisplayName("제품이 존재하지 않을 경우 빈 리스트를 반환한다.")
        @Test
        void checkGetProductList_success_emptyList() {
            // given
            List<Product> productList = new ArrayList<>();
            List<Long> productIdList = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                productIdList.add((long) i);
            }
            when(productRepository.productFindByIds(productIdList)).thenReturn(productList);

            // when
            List<Product> result = productService.getProductListByProductIds(productIdList);

            // then
            assertEquals(result.size(), 0);
        }
    }

    @Nested
    @DisplayName("ProductService 의 getProductQuantityListWithLock 메서드 테스트")
    class GetProductQuantityListWithLock {
        // TODO: 테트스토드작성
//        @DisplayName("")
//        @Test
//        void getProductQuantityListWithLock() {
//
//        }
//
//        @DisplayName("")
//        @Test
//        void getProductQuantityListWithLock() {
//
//        }
    }
}
