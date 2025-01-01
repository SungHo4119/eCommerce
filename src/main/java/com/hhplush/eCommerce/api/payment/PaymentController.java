package com.hhplush.eCommerce.api.payment;

import com.hhplush.eCommerce.common.dto.payment.request.RequestCreatePaymentDTO;
import com.hhplush.eCommerce.common.dto.payment.response.ResponseCreatePayment;
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

    private final IPaymentService paymentService;

    @PostMapping
    public ResponseEntity<ResponseCreatePayment> createPayment(
        @RequestBody RequestCreatePaymentDTO requestCreatePaymentDTO
    ) {
        return ResponseEntity.ok(
            ResponseCreatePayment.builder().paymentId(1).orderId(1).userCouponId(1).amount(5000)
                .paymentAt(
                    LocalDateTime.now()).build()
        );
    }
}
