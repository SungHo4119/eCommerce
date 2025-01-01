package com.hhplush.eCommerce.api.user;


import com.hhplush.eCommerce.common.dto.user.request.RequestChargeUserPointDTO;
import com.hhplush.eCommerce.common.dto.user.response.ResponseChargeUserPointDTO;
import com.hhplush.eCommerce.common.dto.user.response.ResponseGetUserCoupon;
import com.hhplush.eCommerce.common.dto.user.response.ResponseGetUserDTO;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final IUserService userService;

    // TODO: 유저 정보(포인트) 조회
    @GetMapping("/{userId}")
    public ResponseEntity<ResponseGetUserDTO> getUser(
        @PathVariable("userId") Integer userId
    ) {
        return ResponseEntity.ok(
            ResponseGetUserDTO.builder().userId(1).userName("이름").point(100).build()
        );
    }

    // TODO: 유저 포인트 충전
    @PostMapping("/{userId}/charge")
    public ResponseEntity<ResponseChargeUserPointDTO> chargeUserPoint(
        @PathVariable("userId") Integer userId,
        @RequestBody RequestChargeUserPointDTO requestChargeUserPointDTO
    ) {
        return ResponseEntity.ok(
            ResponseChargeUserPointDTO.builder().userId(1).userName("이름").point(100).build()
        );
    }

    @GetMapping("/{userId}/coupon")
    public ResponseEntity<List<ResponseGetUserCoupon>> getUserCoupon(
        @PathVariable("userId") Integer userId
    ) {
        return ResponseEntity.ok(
            List.of(
                ResponseGetUserCoupon.builder().userCouponId(1).coupon(
                    ResponseGetUserCoupon.Coupon.builder().couponId(1).couponName("쿠폰")
                        .discountAmount(100).build()
                ).userId(1).use(false).useAt(null).createdAt(LocalDateTime.now()).build(),
                ResponseGetUserCoupon.builder().userCouponId(2).coupon(
                    ResponseGetUserCoupon.Coupon.builder().couponId(2).couponName("쿠폰2")
                        .discountAmount(200).build()
                ).userId(1).use(false).useAt(null).createdAt(LocalDateTime.now()).build()
            )
        );
    }
}
