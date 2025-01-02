package com.hhplush.eCommerce.api.payment;

import com.hhplush.eCommerce.api.payment.dto.request.RequestCreatePaymentDTO;
import com.hhplush.eCommerce.api.payment.dto.response.ResponseCreatePayment;
import com.hhplush.eCommerce.business.payment.PaymentService;
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

    private final PaymentService paymentService;

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
