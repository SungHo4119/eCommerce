package com.hhplush.eCommerce.api.products;

import com.hhplush.eCommerce.api.products.dto.response.ResponseProductListDTO;
import com.hhplush.eCommerce.api.products.dto.response.ResponseProductTopDTO;
import com.hhplush.eCommerce.business.product.ProductUseCase;
import com.hhplush.eCommerce.domain.product.Product;
import com.hhplush.eCommerce.domain.product.ProductTop;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    private final ProductUseCase productUseCase;


    @Operation(summary = "상품 목록 조회")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "상품 목록 조회 성공",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ResponseProductListDTO.class,
                    example =
                        "[{\"productId\":1,\"productName\":\"상품1\",\"price\":1000,\"productState\":\"IN_STOCK\"}"
                            + ",{\"productId\":2,\"productName\":\"상품2\",\"price\":2000,\"productState\":\"OUT_OF_STOCK\"}]"))),
    })
    @GetMapping()
    public ResponseEntity<List<ResponseProductListDTO>> listProducts() {
        List<Product> products = productUseCase.getProducts();
        return ResponseEntity.ok(
            products.stream().map(
                product -> ResponseProductListDTO.builder()
                    .productId(product.getProductId())
                    .productName(product.getProductName())
                    .price(product.getPrice())
                    .productState(product.getProductState())
                    .build()
            ).toList()
        );
    }

    @Operation(summary = "상위 상품 목록 조회")
    @GetMapping("/top")
    public ResponseEntity<List<ResponseProductTopDTO>> topProducts(
    ) {
        List<ProductTop> products = productUseCase.getTopProducts();

        return ResponseEntity.ok(
            products.stream().map(
                product -> ResponseProductTopDTO.builder()
                    .productTopId(product.getProductTopId())
                    .productId(product.getProductId())
                    .productName(product.getProductName())
                    .price(product.getPrice())
                    .productState(product.getProductState())
                    .product_rank(product.getProductRank())
                    .createAt(product.getCreateAt())
                    .build()
            ).toList()
        );
    }
}
