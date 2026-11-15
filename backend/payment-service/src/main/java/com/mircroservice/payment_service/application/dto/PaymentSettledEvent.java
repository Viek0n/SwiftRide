package com.mircroservice.payment_service.application.dto;

import java.time.Instant;

public record PaymentSettledEvent(
        String eventId,
        String eventType,
        String tripId,
        String riderId,
        long amount,
        String currency,
        String gatewayTransactionId,
        Instant occurredAt) {
}
