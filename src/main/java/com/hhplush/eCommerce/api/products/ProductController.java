package com.hhplush.eCommerce.api.products;

import com.hhplush.eCommerce.common.dto.product.response.ResponseProductListDTO;
import com.hhplush.eCommerce.common.dto.product.response.ResponseProductTopDTO;
import com.hhplush.eCommerce.domain.entitiy.ProductState;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {

    private final IProductService productService;

    @GetMapping("/list")
    public ResponseEntity<List<ResponseProductListDTO>> listProducts() {
        return ResponseEntity.ok(
            List.of(
                ResponseProductListDTO.builder().productId(1).productName("product1").price(1000)
                    .productState(
                        ProductState.IN_STOCK)
                    .build(),
                ResponseProductListDTO.builder().productId(2).productName("product2").price(2000)
                    .productState(
                        ProductState.IN_STOCK)
                    .build(),
                ResponseProductListDTO.builder().productId(3).productName("product3").price(3000)
                    .productState(
                        ProductState.OUT_OF_STOCK)
                    .build()
            )
        );
    }

    @GetMapping("/top")
    public ResponseEntity<List<ResponseProductTopDTO>> topProducts() {
        return ResponseEntity.ok(
            List.of(
                new ResponseProductTopDTO(1, 1, "product1", 1000, ProductState.IN_STOCK, 1,
                    LocalDateTime.now()),
                new ResponseProductTopDTO(2, 2, "product2", 2000, ProductState.IN_STOCK, 2,
                    LocalDateTime.now()),
                new ResponseProductTopDTO(3, 3, "product3", 3000, ProductState.OUT_OF_STOCK, 3,
                    LocalDateTime.now())
            )
        );
    }
}
