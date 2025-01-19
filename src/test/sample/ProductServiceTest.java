package com.hhplush.eCommerce.unit;

import static com.hhplush.eCommerce.common.exception.message.ExceptionMessage.PRODUCT_LIMIT_EXCEEDED;
import static com.hhplush.eCommerce.common.exception.message.ExceptionMessage.PRODUCT_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.hhplush.eCommerce.common.exception.custom.LimitExceededException;
import com.hhplush.eCommerce.common.exception.custom.ResourceNotFoundException;
import com.hhplush.eCommerce.domain.order.OrderProduct;
import com.hhplush.eCommerce.domain.product.IProductRepository;
import com.hhplush.eCommerce.domain.product.Product;
import com.hhplush.eCommerce.domain.product.ProductQuantity;
import com.hhplush.eCommerce.domain.product.ProductService;
import com.hhplush.eCommerce.domain.product.ProductState;
import com.hhplush.eCommerce.domain.product.ProductTop;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
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
    IProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // 제품 목록 조회
    @Nested
    @DisplayName("ProductLoader 의 getProductList 메서드 테스트")
    class GetProductsTests {

        @Test
        void getProductList_성공() {
            // given
            Product product1 = Product.builder().productId(1L).productName("product1").price(100L)
                .productState(
                    ProductState.IN_STOCK).build();
            Product product2 = Product.builder().productId(2L).productName("product2").price(100L)
                .productState(
                    ProductState.OUT_OF_STOCK).build();
            List<Product> productList = Arrays.asList(product1, product2);
            when(productRepository.productFindAll()).thenReturn(productList);
            // when
            List<Product> result = productService.getProductList();
            // then
            assertEquals(productList, result);
        }

    }

    // 제품 목록 확인
    @Nested
    @DisplayName("ProductLoader 의 checkGetProductList 메서드 테스트")
    class CheckGetProductListTests {

        @Test
        void checkGetProductList_성공() {
            // given
            List<Long> productIds = Arrays.asList(1L, 2L);
            Product product1 = Product.builder().productId(1L).productName("product1").price(100L)
                .productState(
                    ProductState.IN_STOCK).build();
            Product product2 = Product.builder().productId(2L).productName("product2").price(100L)
                .productState(
                    ProductState.OUT_OF_STOCK).build();
            List<Product> productList = Arrays.asList(product1, product2);
            when(productRepository.productFindByIds(productIds)).thenReturn(productList);
            // when
            List<Product> result = productService.checkGetProductList(productIds);
            // then
            assertEquals(productList, result);
        }
    }

    @Nested
    @DisplayName("ProductLoader 의 getProductQuantityListWithLock 메서드 테스트")
    class GetProductQuantityListWithLockTests {

        @Test
        void getProductQuantityListWithLock() {
            // given
            List<Long> productIds = Arrays.asList(1L, 2L);
            ProductQuantity productQuantity1 = ProductQuantity.builder().productId(1L).quantity(10L)
                .build();
            ProductQuantity productQuantity2 = ProductQuantity.builder().productId(2L).quantity(20L)
                .build();
            List<ProductQuantity> productQuantityList = Arrays.asList(productQuantity1,
                productQuantity2);
            when(productRepository.productQuantityFindByIdsWithLock(productIds)).thenReturn(
                productQuantityList);
            // when
            List<ProductQuantity> result = productService.getProductQuantityListWithLock(
                productIds);
            // then
            assertEquals(productQuantityList, result);
        }
    }

    @Nested
    @DisplayName("ProductLoader 의 validateProductQuantitie 메서드 테스트")
    class ValidateProductQuantitiesTests {

        @Test
        void PRODUCT_NOT_FOUND_ResourceNotFoundException() {
            // given
            List<OrderProduct> orderProductList = Collections.singletonList(
                OrderProduct.builder().orderProductId(1L).orderId(1L).productId(1L).quantity(5L)
                    .build()
            );
            List<ProductQuantity> productQuantityList = Arrays.asList(
                ProductQuantity.builder().productQuantityId(1L).productId(1L).quantity(10L).build(),
                ProductQuantity.builder().productQuantityId(2L).productId(2L).quantity(20L).build()
            );
            // when
            ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> {
                    productService.validateProductQuantitie(orderProductList, productQuantityList);
                });
            // then
            assertEquals(PRODUCT_NOT_FOUND, exception.getMessage());
        }

        @Test
        void PRODUCT_LIMIT_EXCEEDED_LimitExceededException() {
            // given
            List<OrderProduct> orderProductList = Arrays.asList(
                OrderProduct.builder().orderProductId(1L).orderId(1L).productId(1L).quantity(5L)
                    .build(),
                OrderProduct.builder().orderProductId(1L).orderId(1L).productId(2L).quantity(10L)
                    .build()
            );
            List<ProductQuantity> productQuantityList = Arrays.asList(
                ProductQuantity.builder().productQuantityId(1L).productId(1L).quantity(10L).build(),
                ProductQuantity.builder().productQuantityId(2L).productId(2L).quantity(5L).build()
            );
            // when
            LimitExceededException exception = assertThrows(LimitExceededException.class,
                () -> {
                    productService.validateProductQuantitie(orderProductList, productQuantityList);
                });
            // then
            assertEquals(PRODUCT_LIMIT_EXCEEDED, exception.getMessage());
        }

        @Test
        void validateProductQuantitie_성공() {
            // given
            List<OrderProduct> orderProductList = Arrays.asList(
                OrderProduct.builder().orderProductId(1L).orderId(1L).productId(1L).quantity(5L)
                    .build(),
                OrderProduct.builder().orderProductId(1L).orderId(1L).productId(2L).quantity(10L)
                    .build()
            );
            List<ProductQuantity> productQuantityList = Arrays.asList(
                ProductQuantity.builder().productQuantityId(1L).productId(1L).quantity(10L).build(),
                ProductQuantity.builder().productQuantityId(1L).productId(2L).quantity(20L).build()
            );
            when(productRepository.productQuantityFindByIdsWithLock(
                Arrays.asList(1L, 2L))).thenReturn(productQuantityList);
            // when
            List<ProductQuantity> result = productService.validateProductQuantitie(orderProductList,
                productQuantityList);
            // then
            assertEquals(productQuantityList, result);
            // 재고 차감이 이루워졌는지 확인
            assertEquals(5L, productQuantityList.get(0).getQuantity());
            assertEquals(10L, productQuantityList.get(1).getQuantity());
        }
    }

    @Nested
    @DisplayName("ProductLoader 의 saveAllProductQuantity 메서드 테스트")
    class SaveAllProductQuantityTests {

        @Test
        void saveAllProductQuantity_성공() {
            // given
            List<ProductQuantity> productQuantities = Arrays.asList(
                ProductQuantity.builder().productQuantityId(1L).productId(1L).quantity(5L).build(),
                ProductQuantity.builder().productQuantityId(2L).productId(2L).quantity(10L).build()
            );
            List<Product> products = Arrays.asList(
                Product.builder().productId(1L).productName("product1").price(100L)
                    .productState(ProductState.IN_STOCK).build(),
                Product.builder().productId(2L).productName("product2").price(100L)
                    .productState(ProductState.OUT_OF_STOCK).build()
            );

            // when & then
            productService.saveAllProductQuantity(productQuantities, products);
        }


    }

    @Nested
    @DisplayName("ProductLoader 의 cancelProductQuantity 메서드 테스트")
    class CancelProductQuantityTests {

        @Test
        void PRODUCT_NOT_FOUND_ResourceNotFoundException() {
            // given
            List<ProductQuantity> productQuantities = Arrays.asList(
                ProductQuantity.builder().productQuantityId(1L).productId(1L).quantity(5L).build(),
                ProductQuantity.builder().productQuantityId(2L).productId(2L).quantity(10L).build()
            );
            List<OrderProduct> orderProductList = Collections.singletonList(
                OrderProduct.builder().orderProductId(1L).orderId(1L).productId(1L).quantity(5L)
                    .build()
            );
            // when
            ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> productService.cancelProductQuantity(productQuantities, orderProductList));
            // then
            assertEquals(PRODUCT_NOT_FOUND, exception.getMessage());
        }

        @Test
        void cancelProductQuantity_성공() {
            // given
            List<ProductQuantity> productQuantities = Arrays.asList(
                ProductQuantity.builder().productQuantityId(1L).productId(1L).quantity(5L).build(),
                ProductQuantity.builder().productQuantityId(2L).productId(2L).quantity(10L).build()
            );
            List<OrderProduct> orderProductList = Arrays.asList(
                OrderProduct.builder().orderProductId(1L).orderId(1L).productId(1L).quantity(5L)
                    .build(),
                OrderProduct.builder().orderProductId(1L).orderId(1L).productId(2L).quantity(10L)
                    .build()
            );
            // when & then
            productService.cancelProductQuantity(productQuantities, orderProductList);

            // 재고 증가 확인
            assertEquals(10L, productQuantities.get(0).getQuantity());
            assertEquals(20L, productQuantities.get(1).getQuantity());
        }
    }

    @Nested
    @DisplayName("ProductLoader 의 getTopProductList 메서드 테스트")
    class GetTopProductListTests {

        @Test
        void function() {
            // given
            LocalDate toDay = LocalDate.now();
            List<ProductTop> productTopList = Arrays.asList(
                ProductTop.builder().productId(1L).productName("product1").price(100L).build(),
                ProductTop.builder().productId(2L).productName("product2").price(200L).build()
            );
            when(productRepository.findProductTopByToDay(toDay)).thenReturn(productTopList);
            // when
            List<ProductTop> result = productService.getTopProductList(toDay);
            // then
            assertEquals(productTopList, result);
        }
    }
}
