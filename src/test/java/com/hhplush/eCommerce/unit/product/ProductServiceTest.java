package com.hhplush.eCommerce.unit.product;

import static com.hhplush.eCommerce.common.exception.message.ExceptionMessage.PRODUCT_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hhplush.eCommerce.common.exception.custom.ResourceNotFoundException;
import com.hhplush.eCommerce.domain.order.OrderProduct;
import com.hhplush.eCommerce.domain.product.IProductRepository;
import com.hhplush.eCommerce.domain.product.Product;
import com.hhplush.eCommerce.domain.product.ProductQuantity;
import com.hhplush.eCommerce.domain.product.ProductService;
import com.hhplush.eCommerce.domain.product.ProductState;
import com.hhplush.eCommerce.domain.product.ProductTop;
import java.time.LocalDate;
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

        @DisplayName("제품과 조회하려는 제품 아이디가 다른 경우 ResourceNotFoundException 예외를 발생한다.")
        @Test
        void PRODUCT_NOT_FOUND_ResourceNotFoundException() {
            // given
            List<Long> productIdList = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                productIdList.add((long) i);
            }
            when(productRepository.productQuantityFindByIdsWithLock(productIdList)).thenReturn(
                new ArrayList<>());

            // when
            ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> productService.getProductQuantityListWithLock(productIdList));
            // then
            assertEquals(exception.getMessage(), PRODUCT_NOT_FOUND);
        }

        @DisplayName("제품과 조회하려는 제품 아이디가 같은 경우 제품 수량 리스트를 반환한다.")
        @Test
        void getProductQuantityListWithLock_success() {
            // given
            List<ProductQuantity> productQuantityList = new ArrayList<>();
            List<Long> productIdList = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                productQuantityList.add(ProductQuantity.builder().productId((long) i).build());
                productIdList.add((long) i);
            }
            when(productRepository.productQuantityFindByIdsWithLock(productIdList)).thenReturn(
                productQuantityList);

            // when
            List<ProductQuantity> result = productService.getProductQuantityListWithLock(
                productIdList);

            // then
            assertEquals(productQuantityList, result);
        }
    }

    @Nested
    @DisplayName("ProductService 의 saveAllProductQuantity 메서드 테스트")
    class SaveAllProductQuantity {

        @DisplayName("제품 수량 리스트와 제품 리스트를 받아 재고 업데이트를 성공한다.")
        @Test
        void saveAllProductQuantity_success() {
            // given
            List<ProductQuantity> productQuantities = new ArrayList<>();
            List<Product> emptyProducts = new ArrayList<>();

            for (int i = 1; i < 5; i++) {
                productQuantities.add(ProductQuantity.builder().productId((long) i).build());
                emptyProducts.add(
                    Product.builder().productId((long) i).productState(ProductState.OUT_OF_STOCK)
                        .build());
            }
            // when
            productService.saveAllProductQuantity(productQuantities, emptyProducts);
            // then
            verify(productRepository).productQuantitySaveAll(productQuantities);
            verify(productRepository).productSaveAll(emptyProducts);
        }

        @DisplayName("제품 수량 리스트와 제품 리스트를(빈) 받아 재고 업데이트를 성공한다.")
        @Test
        void saveAllProductQuantity_success2() {
            // given
            List<ProductQuantity> productQuantities = new ArrayList<>();
            List<Product> emptyProducts = new ArrayList<>();

            for (int i = 1; i < 5; i++) {
                productQuantities.add(ProductQuantity.builder().productId((long) i).build());
            }
            // when
            productService.saveAllProductQuantity(productQuantities, emptyProducts);
            // then
            verify(productRepository).productQuantitySaveAll(productQuantities);
        }
    }

    @Nested
    @DisplayName("ProductService 의 findEmptyProducts 메서드 테스트")
    class FindEmptyProducts {

        @DisplayName("제품 리스트 중 OUT_OF_STOCK 인 제품만 반환한다.")
        @Test
        void findEmptyProducts_success() {
            // given
            List<Product> products = new ArrayList<>();
            ProductState productState = ProductState.OUT_OF_STOCK;
            for (int i = 1; i < 5; i++) {
                if (i == 4) {
                    productState = ProductState.IN_STOCK;
                }
                products.add(
                    Product.builder().productId((long) i).productState(productState)
                        .build());
            }
            // when
            List<Product> result = productService.findEmptyProducts(products);
            // then
            products.remove(3);
            assertEquals(products, result);
        }
    }

    @Nested
    @DisplayName("ProductService 의 cancelProductQuantity 메서드 테스트")
    class CancelProductQuantity {

        @DisplayName("주문 상품 리스트와 제품 수량 리스트를 받아 재고 복구를 성공한다.")
        @Test
        void cancelProductQuantity_success() {
            // given
            List<ProductQuantity> productQuantities = new ArrayList<>();
            List<OrderProduct> orderProductList = new ArrayList<>();
            Long isQuantity = 5L;
            Long beforeQuantity = 10L;
            Long resultQuantity = 15L;

            for (int i = 1; i < 5; i++) {
                productQuantities.add(
                    ProductQuantity.builder().productId((long) i).quantity(beforeQuantity)
                        .build());
                orderProductList.add(
                    OrderProduct.builder().productId((long) i).quantity(isQuantity).build());
            }
            // when
            productService.cancelProductQuantity(productQuantities, orderProductList);
            // then
            for (ProductQuantity productQuantity : productQuantities) {
                assertEquals(productQuantity.getQuantity(), resultQuantity);
            }
            verify(productRepository).productQuantitySaveAll(productQuantities);
        }
    }

    @Nested
    @DisplayName("ProductService 의 getTopProductList 메서드 테스트")
    class CalculateOrderAmount {

        @DisplayName("주문 상품 리스트와 제품 리스트를 받아 주문 금액을 계산한다.")
        @Test
        void getTopProductList_success() {
            // given
            List<ProductTop> productList = new ArrayList<>();
            LocalDate toDay = LocalDate.now();
            for (int i = 1; i < 5; i++) {
                productList.add(
                    ProductTop.builder().productId((long) i).price((long) i).createAt(toDay)
                        .build());
            }
            when(productRepository.findProductTopByToDay(toDay)).thenReturn(productList);

            // when
            List<ProductTop> result = productService.getTopProductList(toDay);
            // then
            assertEquals(productList, result);
        }
    }
}
