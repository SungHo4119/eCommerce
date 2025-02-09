package com.hhplush.eCommerce.api.coupon;

import static com.hhplush.eCommerce.common.exception.message.ExceptionMessage.INVALID_ID;

import com.hhplush.eCommerce.api.coupon.dto.request.RequestIssueCouponDTO;
import com.hhplush.eCommerce.api.coupon.dto.response.ResponseIssueCouponDTO;
import com.hhplush.eCommerce.api.coupon.dto.response.ResponseIssueCouponDTOV2;
import com.hhplush.eCommerce.business.coupon.CouponUseCase;
import com.hhplush.eCommerce.common.exception.ErrorResponse;
import com.hhplush.eCommerce.domain.coupon.UserCoupon;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/api/coupons")
public class CouponController {

    private final CouponUseCase couponUseCase;

    @Operation(summary = "쿠폰 발급")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Coupon issued successfully",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ResponseIssueCouponDTO.class,
                    example = "{\"userCouponId\":1,\"couponId\":1,\"userId\":1,\"couponUse\":false,\"useAt\":null,\"createAt\":\"2023-10-10T10:00:00\"}"))),
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
                    @ExampleObject(name = "User Not Found", value = "{\"code\":\"404\",\"message\":\"사용자를 찾을 수 없습니다.\"}"),
                    @ExampleObject(name = "Coupon Not Found", value = "{\"code\":\"404\",\"message\":\"쿠폰을 찾을 수 없습니다.\"}")
                })),
        @ApiResponse(responseCode = "409", description = "Conflict",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class),
                examples = {
                    @ExampleObject(name = "Coupon Already Exists", value = "{\"code\":\"409\",\"message\":\"이미 보유한 쿠폰입니다.\"}"),
                    @ExampleObject(name = "Coupon Limit Exceeded", value = "{\"code\":\"409\",\"message\":\"쿠폰 발급 한도를 초과하였습니다.\"}")
                }))
    })
    @PostMapping("/{couponId}/issued")
    public ResponseEntity<ResponseIssueCouponDTO> issueCoupon(
        @Schema(description = "쿠폰 ID", example = "1")
        @PathVariable("couponId") @Min(value = 1, message = INVALID_ID) Long couponId,
        @Valid @RequestBody RequestIssueCouponDTO requestIssueCouponDTO
    ) {
        UserCoupon userCoupon = couponUseCase.issueCoupon(couponId,
            requestIssueCouponDTO.userId());
        return
            ResponseEntity.ok(ResponseIssueCouponDTO.builder()
                .userCouponId(userCoupon.getUserCouponId())
                .couponId(userCoupon.getCoupon().getCouponId())
                .userId(userCoupon.getUserId())
                .couponUse(userCoupon.getCouponUse())
                .useAt(userCoupon.getUseAt())
                .createAt(userCoupon.getCreateAt())
                .build());
    }


    @PostMapping("/{couponId}/issued/V2")
    public ResponseEntity<ResponseIssueCouponDTOV2> issueCouponV2(
        @Schema(description = "쿠폰 ID", example = "1")
        @PathVariable("couponId") @Min(value = 1, message = INVALID_ID) Long couponId,
        @Valid @RequestBody RequestIssueCouponDTO requestIssueCouponDTO
    ) {
        couponUseCase.issueCouponRequest(couponId,
            requestIssueCouponDTO.userId());
        return
            ResponseEntity.ok(ResponseIssueCouponDTOV2.builder().result("SUCCESS").build());
    }
}
