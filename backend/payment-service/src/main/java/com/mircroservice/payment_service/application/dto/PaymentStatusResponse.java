package com.mircroservice.payment_service.application.dto;

import java.time.Instant;

public record PaymentStatusResponse(
        String tripId,
        long amount,
        String currency,
        String status,
        String latestAttemptId,
        String latestAttemptStatus,
        Instant updatedAt) {
}
