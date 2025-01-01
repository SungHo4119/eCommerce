package com.hhplush.eCommerce.api.coupon;

import com.hhplush.eCommerce.common.dto.coupon.request.RequestIssueCouponDTO;
import com.hhplush.eCommerce.common.dto.coupon.response.ResponseIssueCouponDTO;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/coupons")
public class CouponController {

    private final ICouponService couponService;

    // TODO: 쿠폰 발급
    @PostMapping("/{couponId}/issued")
    public ResponseEntity<ResponseIssueCouponDTO> issueCoupon(
        @PathVariable("couponId") Integer couponId,
        @RequestBody RequestIssueCouponDTO requestIssueCouponDTO
    ) {
        return ResponseEntity.ok(
            ResponseIssueCouponDTO.builder().userCouponId(1).couponId(1).userId(1).use(false)
                .useAt(null)
                .createdAt(
                    LocalDateTime.now()).build()
        );
    }

}
