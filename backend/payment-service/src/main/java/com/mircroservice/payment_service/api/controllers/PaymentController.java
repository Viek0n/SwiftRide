package com.mircroservice.payment_service.api.controllers;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mircroservice.payment_service.api.PaymentRoute;
import com.mircroservice.payment_service.application.dto.CheckoutResponse;
import com.mircroservice.payment_service.application.use_cases.ProcessPayment;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(PaymentRoute.PAYMENTS)
public class PaymentController {
    private final ProcessPayment paymentService;

    public PaymentController(ProcessPayment paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping(PaymentRoute.CHECKOUT)
    public CheckoutResponse createCheckout(
            @PathVariable String tripId,
            @RequestHeader("X-Rider-Id") String riderId,
            @RequestHeader("Idempotency-Key") String idempotencyKey,
            HttpServletRequest request) {
        return paymentService.createCheckout(tripId, riderId, idempotencyKey, request.getRemoteAddr());
    }
}
