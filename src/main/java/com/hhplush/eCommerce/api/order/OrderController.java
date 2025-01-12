package com.hhplush.eCommerce.api.order;

import com.hhplush.eCommerce.api.order.dto.request.RequestCreateOrderDTO;
import com.hhplush.eCommerce.api.order.dto.request.RequestCreateOrderDTO.RequestProducts;
import com.hhplush.eCommerce.api.order.dto.response.ResponseCreateOrderDTO;
import com.hhplush.eCommerce.business.order.OrderUseCase;
import com.hhplush.eCommerce.common.exception.ErrorResponse;
import com.hhplush.eCommerce.domain.order.Order;
import com.hhplush.eCommerce.domain.order.OrderProduct;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderUseCase orderUseCase;


    @Operation(summary = "주문 생성")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Order created successfully",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ResponseCreateOrderDTO.class,
                    example = "{\"orderId\":1,\"userId\":1,\"orderStatus\":\"PENDING\",\"orderAt\":\"2023-10-10T10:00:00\"}"))),
        @ApiResponse(responseCode = "400", description = "Bad Request",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class),
                examples = {
                    @ExampleObject(name = "INVALID_ID", value = "{\"code\":\"400\",\"message\":\"유효하지 않은 ID 양식 입니다.\"}"),
                    @ExampleObject(name = "INVALID_QUANTITY", value = "{\"code\":\"400\",\"message\":\"유효하지 않은 quantity 양식 입니다.\"}"),
                })),
        @ApiResponse(responseCode = "404", description = "Bad Request",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class),
                examples = {
                    @ExampleObject(name = "USER_NOT_FOUND", value = "{\"code\":\"404\",\"message\":\"사용자를 찾을 수 없습니다.\"}"),
                    @ExampleObject(name = "PRODUCT_NOT_FOUND", value = "{\"code\":\"404\",\"message\":\"제품을 찾을 수 없습니다.\"}"),
                    @ExampleObject(name = "COUPON_NOT_FOUND", value = "{\"code\":\"404\",\"message\":\"쿠폰을 찾을 수 없습니다\"}"),
                })),
        @ApiResponse(responseCode = "409", description = "ConflictException",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class),
                examples = {
                    @ExampleObject(name = "COUPON_USE_ALREADY_EXISTS", value = "{\"code\":\"409\",\"message\":\"이미 사용한 쿠폰입니다.\"}"),
                    @ExampleObject(name = "PRODUCT_LIMIT_EXCEEDED", value = "{\"code\":\"409\",\"message\":\"제품 재고가 부족합니다.\"}"),
                }))
    })
    @PostMapping
    public ResponseEntity<ResponseCreateOrderDTO> createOrder(
        @RequestBody RequestCreateOrderDTO requestCreateOrderDTO
    ) {

        Long userId = requestCreateOrderDTO.userId();
        Long userCouponId = requestCreateOrderDTO.userCouponId();
        List<RequestProducts> requestProducts = requestCreateOrderDTO.product();

        // 주문 상품 생성 ( 주문 상품 리스트 생성 )
        List<OrderProduct> orderProductList = requestProducts.stream().map(
            requestProduct ->
                OrderProduct.builder().productId(requestProduct.productId())
                    .quantity(requestProduct.quantity()).build()
        ).toList();

        Order order = orderUseCase.createOrder(userId, userCouponId,
            orderProductList);

        return ResponseEntity.ok(ResponseCreateOrderDTO.builder()
            .orderId(order.getOrderId())
            .userId(order.getUserId())
            .orderStatus(order.getOrderState())
            .orderAt(order.getOrderAt())
            .build());
    }
}
