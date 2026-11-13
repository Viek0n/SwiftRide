package com.mircroservice.payment_service.application.dto;

import java.time.Instant;

public record RideCompletedEvent(
        String tripId,
        String riderId,
        long amount,
        String currency,
        String quoteId,
        Instant occurredAt) {
}
