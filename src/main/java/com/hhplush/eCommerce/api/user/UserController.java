package com.hhplush.eCommerce.api.user;


import static com.hhplush.eCommerce.common.exception.message.ExceptionMessage.INVALID_ID;

import com.hhplush.eCommerce.api.user.dto.request.RequestChargeUserPointDTO;
import com.hhplush.eCommerce.api.user.dto.response.ResponseChargeUserPointDTO;
import com.hhplush.eCommerce.api.user.dto.response.ResponseGetUserCoupon;
import com.hhplush.eCommerce.api.user.dto.response.ResponseGetUserDTO;
import com.hhplush.eCommerce.business.user.UserUseCase;
import com.hhplush.eCommerce.common.exception.ErrorResponse;
import com.hhplush.eCommerce.domain.coupon.UserCoupon;
import com.hhplush.eCommerce.domain.user.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserUseCase userService;

    @Operation(summary = "유저 조회")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "유저 정보 조회 성공",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ResponseGetUserDTO.class,
                    example = "{\"userId\":1,\"userName\":\"이름\",\"point\":100}"))),
        @ApiResponse(responseCode = "400", description = "INVALID_ID",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class,
                    example = "{\"code\":\"400\",\"message\":\"유효하지 않은 ID 입니다.\"}"))),
        @ApiResponse(responseCode = "404", description = "USER_NOT_FOUND",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class,
                    example = "{\"code\":\"404\",\"message\":\"사용자를 찾을 수 없습니다.\"}"))),
    })
    @GetMapping("/{userId}")
    public ResponseEntity<ResponseGetUserDTO> getUser(
        @PathVariable("userId") @Min(value = 1, message = INVALID_ID)
        @Schema(description = "유저 ID", example = "1") Long userId
    ) {
        User user = userService.getUser(userId);
        return ResponseEntity.ok(
            ResponseGetUserDTO.builder().userId(user.getUserId()).userName(user.getUserName())
                .point(user.getPoint()).build()
        );
    }

    @Operation(summary = "유저 포인트 충전")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "유저 포인트 충전 성공",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ResponseGetUserDTO.class,
                    example = "{\"userId\":1,\"userName\":\"이름\",\"point\":100}"))),

        @ApiResponse(responseCode = "400", description = "INVALID_ID",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class,
                    example = "{\"code\":\"400\",\"message\":\"유효하지 않은 ID 입니다.\"}"))),
        @ApiResponse(responseCode = "404", description = "USER_NOT_FOUND",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class,
                    example = "{\"code\":\"404\",\"message\":\"사용자를 찾을 수 없습니다.\"}"))),
    })
    @PostMapping("/{userId}/charge")
    public ResponseEntity<ResponseChargeUserPointDTO> chargeUserPoint(
        @PathVariable("userId") @Min(value = 1, message = INVALID_ID)
        @Schema(description = "유저 ID", example = "1") Long userId,
        @Valid @RequestBody RequestChargeUserPointDTO requestChargeUserPointDTO
    ) {
        User user = userService.chargeUserPoint(userId, requestChargeUserPointDTO.point());
        return ResponseEntity.ok(
            ResponseChargeUserPointDTO.builder().userId(user.getUserId())
                .userName(user.getUserName())
                .point(user.getPoint()).build()
        );
    }

    @Operation(summary = "유저 쿠폰 조회")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "유저 쿠폰 조회 성공",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ResponseGetUserCoupon.class,
                    example = "{\"userCouponId\":1,\"userId\":1,\"couponUse\":\"true\",\"useAt\":null,\"createAt\":\"2021-10-10T00:00:00\",\"coupon\":{\"couponId\":1,\"couponName\":\"쿠폰\",\"discountAmount\":100}}"))),

        @ApiResponse(responseCode = "400", description = "INVALID_ID",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class,
                    example = "{\"code\":\"400\",\"message\":\"유효하지 않은 ID 입니다.\"}"))),
    })
    @GetMapping("/{userId}/coupon")
    public ResponseEntity<List<ResponseGetUserCoupon>> getUserCoupon(
        @PathVariable("userId") @Min(value = 1, message = INVALID_ID) Long userId
    ) {
        List<UserCoupon> userCoupon = userService.getUserCoupon(userId);

        return ResponseEntity.ok(userCoupon.stream()
            .map(coupon -> ResponseGetUserCoupon.builder()
                .userCouponId(coupon.getUserCouponId())
                .userId(coupon.getUserId())
                .couponUse(coupon.getCouponUse())
                .useAt(coupon.getUseAt())
                .createAt(coupon.getCreateAt())
                .coupon(
                    ResponseGetUserCoupon.Coupon.builder()
                        .couponId(coupon.getCoupon().getCouponId())
                        .couponName(coupon.getCoupon().getCouponName())
                        .discountAmount(coupon.getCoupon().getDiscountAmount())
                        .build()
                )
                .build())
            .toList());
    }
}
