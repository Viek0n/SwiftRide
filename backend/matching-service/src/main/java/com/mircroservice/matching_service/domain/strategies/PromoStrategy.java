package com.mircroservice.matching_service.domain.strategies;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import com.mircroservice.matching_service.domain.constants.PriceMultiplier;

@Component("promoPricing")
public class PromoStrategy implements PricingStrategy {
    @Override
    public BigDecimal multiplier() {
        return BigDecimal.valueOf(PriceMultiplier.PROMO);
    }
}
