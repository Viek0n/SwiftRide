package com.mircroservice.matching_service.domain.strategies;

import java.math.BigDecimal;

public interface PricingStrategy {
    BigDecimal multiplier();
}
