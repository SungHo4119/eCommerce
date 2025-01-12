package com.hhplush.eCommerce.api.payment;

import com.hhplush.eCommerce.api.order.dto.response.ResponseCreateOrderDTO;
import com.hhplush.eCommerce.api.payment.dto.request.RequestCreatePaymentDTO;
import com.hhplush.eCommerce.api.payment.dto.response.ResponseCreatePayment;
import com.hhplush.eCommerce.business.payment.PaymentUseCase;
import com.hhplush.eCommerce.common.exception.ErrorResponse;
import com.hhplush.eCommerce.domain.payment.Payment;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentUseCase paymentUseCase;

    @Operation(summary = "결재")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Order created successfully",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ResponseCreateOrderDTO.class,
                    example = "{\"paymentId\":1,\"orderId\":1,\"paymentAt\":\"2021-08-01T00:00:00\"}"))),
        @ApiResponse(responseCode = "400", description = "Bad Request",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class),
                examples = {
                    @ExampleObject(name = "INVALID_ID", value = "{\"code\":\"400\",\"message\":\"유효하지 않은 ID 양식 입니다.\"}"),
                })),
        @ApiResponse(responseCode = "404", description = "Not Found",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class),
                examples = {
                    @ExampleObject(name = "USER_NOT_FOUND", value = "{\"code\":\"404\",\"message\":\"사용자를 찾을 수 없습니다.\"}"),
                    @ExampleObject(name = "ORDER_NOT_FOUND", value = "{\"code\":\"404\",\"message\":\"주문을 찾을 수 없습니다.\"}"),
                })),
        @ApiResponse(responseCode = "409", description = "Conflict",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class),
                examples = {
                    @ExampleObject(name = "INSUFFICIENT_BALANCE", value = "{\"code\":\"409\",\"message\":\"잔액이 부족합니다.\"}"),
                })),
    })
    @PostMapping
    public ResponseEntity<ResponseCreatePayment> createPayment(
        @RequestBody RequestCreatePaymentDTO requestCreatePaymentDTO
    ) {
        Payment payment = paymentUseCase.processPayment(requestCreatePaymentDTO.orderId());
        return ResponseEntity.ok(
            ResponseCreatePayment.builder()
                .paymentId(payment.getPaymentId())
                .orderId(payment.getOrderId())
                .paymentAt(LocalDateTime.now())
                .build()
        );
    }
}
