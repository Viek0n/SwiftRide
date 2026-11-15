package com.mircroservice.matching_service.domain.strategies;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import com.mircroservice.matching_service.domain.constants.PriceMultiplier;

@Component("surgePricing")
public class SurgePricing implements PricingStrategy {
    @Override
    public BigDecimal multiplier() {
        return BigDecimal.ONE.add(BigDecimal.valueOf(PriceMultiplier.PRICE_SURGING));
    }
}
