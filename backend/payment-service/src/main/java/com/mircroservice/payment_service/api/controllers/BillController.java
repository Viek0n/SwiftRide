package com.mircroservice.payment_service.api.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mircroservice.payment_service.api.PaymentRoute;
import com.mircroservice.payment_service.application.dto.PaymentStatusResponse;
import com.mircroservice.payment_service.application.use_cases.ProcessPayment;

// API đọc trạng thái thanh toán cho một chuyến đi cụ thể. 
// Nó nhận thông tin về chuyến đi và người dùng, sau đó trả về trạng thái thanh toán hiện tại của hóa đơn liên quan đến chuyến đi đó.
@RestController
@RequestMapping(PaymentRoute.PAYMENTS)
public class BillController {
    private final ProcessPayment paymentService;

    public BillController(ProcessPayment paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping(PaymentRoute.PAYMENT_BY_TRIP)
    public PaymentStatusResponse getPaymentStatus(
            @PathVariable String tripId,
            @RequestHeader("X-Rider-Id") String riderId) {
        return paymentService.getStatus(tripId, riderId);
    }
}
