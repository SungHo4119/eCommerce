package com.hhplush.eCommerce.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.hhplush.eCommerce.business.product.ProductLoader;
import com.hhplush.eCommerce.business.product.ProductService;
import com.hhplush.eCommerce.domain.product.Product;
import com.hhplush.eCommerce.domain.product.ProductState;
import com.hhplush.eCommerce.domain.product.ProductTop;
import java.time.LocalDate;
import java.util.Arrays;
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
    ProductLoader productLoader;

    @InjectMocks
    private ProductService productService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Nested
    @DisplayName("ProductService 의 getProducts 메서드 테스트")
    class GetProductsTests {

        @Test
        void getProducts_성공() {
            // given
            Product product1 = Product.builder().productId(1L).productName("product1").price(100L)
                .productState(
                    ProductState.IN_STOCK).build();
            Product product2 = Product.builder().productId(2L).productName("product2").price(200L)
                .productState(
                    ProductState.OUT_OF_STOCK).build();
            List<Product> productList = Arrays.asList(product1, product2);
            when(productLoader.getProductList()).thenReturn(productList);

            // when
            List<Product> result = productService.getProducts();

            // then
            assertEquals(2, result.size());
            assertEquals(product1, result.get(0));
            assertEquals(product2, result.get(1));
        }
    }

    @Nested
    @DisplayName("ProductService 의 getTopProducts 메서드 테스트")
    class GetTopProductsTests {

        @Test
        void getTopProducts_성공() {
            List<ProductTop> productTopList = Arrays.asList(
                ProductTop.builder().productTopId(1L).productId(1L).productName("product1")
                    .price(100L)
                    .productState(ProductState.IN_STOCK).build(),
                ProductTop.builder().productTopId(2L).productId(2L).productName("product2")
                    .price(200L)
                    .productState(ProductState.OUT_OF_STOCK).build()
            );
            LocalDate toDay = LocalDate.now();
            when(productLoader.getTopProductList(toDay)).thenReturn(productTopList);

            // when
            List<ProductTop> result = productService.getTopProducts();

            // then
            assertEquals(result, productTopList);
        }
    }

}