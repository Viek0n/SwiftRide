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

// API tạo checkout cho một chuyến đi cụ thể. Nó nhận thông tin về chuyến đi, người dùng và khóa idempotency để tạo yêu cầu thanh toán và trả về URL thanh toán.
@RestController
@RequestMapping(PaymentRoute.PAYMENTS)
// Lớp này là một controller trong ứng dụng Spring Boot, chịu trách nhiệm xử lý các yêu cầu liên quan đến thanh toán. Nó định nghĩa các endpoint để tạo yêu cầu thanh toán (checkout) cho một chuyến đi cụ thể.
public class PaymentController {
    private final ProcessPayment paymentService;

    //Tạo yêu cầu thanh toán mới cho hóa đơn đã tồn tại
    public PaymentController(ProcessPayment paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping(PaymentRoute.CHECKOUT)
    // Tạo yêu cầu thanh toán (checkout) cho một chuyến đi cụ thể. Yêu cầu này sẽ tạo ra một URL thanh toán mà người dùng có thể sử dụng để thực hiện thanh toán.
    public CheckoutResponse createCheckout(
            @PathVariable String tripId,
            @RequestHeader("X-Rider-Id") String riderId,
            @RequestHeader("Idempotency-Key") String idempotencyKey, //Idempotency giúp request lặp do người dùng bấm hai lần hoặc mạng chập chờn không dễ tạo ra nhiều attempt.
            HttpServletRequest request) {
        return paymentService.createCheckout(tripId, riderId, idempotencyKey, request.getRemoteAddr());
    }
}
