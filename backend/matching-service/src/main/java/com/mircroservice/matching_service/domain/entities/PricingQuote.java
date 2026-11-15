package com.mircroservice.matching_service.domain.entities;

import java.math.BigDecimal;

public record PricingQuote(
    double distanceMeters,
    double durationSeconds,
    BigDecimal baseFare,
    BigDecimal surgeMultiplier,
    BigDecimal toll,
    BigDecimal promotionMultiplier,
    BigDecimal taxRate,
    BigDecimal totalFare
) {}
