package com.hhplush.eCommerce.api.order;

import com.hhplush.eCommerce.common.dto.order.request.RequestCreateOrderDTO;
import com.hhplush.eCommerce.common.dto.order.response.ResponseCreateOrderDTO;
import com.hhplush.eCommerce.domain.entitiy.OrderState;
import java.time.LocalDateTime;
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

    private final IOrderService orderService;

    @PostMapping
    public ResponseEntity<ResponseCreateOrderDTO> createOrder(
        @RequestBody RequestCreateOrderDTO requestCreateOrderDTO
    ) {
        return ResponseEntity.ok(
            ResponseCreateOrderDTO.builder().orderId(1).userId(1).orderStatus(OrderState.PENDING)
                .orderAt(
                    LocalDateTime.now()).build()
        );
    }
}
