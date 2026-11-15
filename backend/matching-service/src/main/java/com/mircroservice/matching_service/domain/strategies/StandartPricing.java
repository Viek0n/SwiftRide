package com.mircroservice.matching_service.domain.strategies;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

@Component("standardPricing")
public class StandartPricing implements PricingStrategy {
    @Override
    public BigDecimal multiplier() {
        return BigDecimal.ONE;
    }
}
