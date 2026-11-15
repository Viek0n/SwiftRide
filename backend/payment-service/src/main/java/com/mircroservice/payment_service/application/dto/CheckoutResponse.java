package com.mircroservice.payment_service.application.dto;

public record CheckoutResponse(
        String tripId,
        String attemptId,
        long amount,
        String currency,
        String status,
        String paymentUrl) {
}
