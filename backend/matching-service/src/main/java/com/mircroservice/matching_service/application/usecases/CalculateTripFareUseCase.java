package com.mircroservice.matching_service.application.usecases;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.mircroservice.matching_service.application.abstractions.RouteInfoProvider;
import com.mircroservice.matching_service.domain.constants.PriceMultiplier;
import com.mircroservice.matching_service.domain.dto.requests.CalculateTripFareRequest;
import com.mircroservice.matching_service.domain.entities.PricingQuote;
import com.mircroservice.matching_service.domain.strategies.PricingStrategy;

@Service
public class CalculateTripFareUseCase {
    private static final int FARE_SCALE = 2;
    private static final int CALCULATION_SCALE = 8;

    private final RouteInfoProvider routeInfoProvider;
    private final PricingStrategy standardPricing;
    private final PricingStrategy surgePricing;
    private final PricingStrategy promoPricing;

    public CalculateTripFareUseCase(
        RouteInfoProvider routeInfoProvider,
        @Qualifier("standardPricing") PricingStrategy standardPricing,
        @Qualifier("surgePricing") PricingStrategy surgePricing,
        @Qualifier("promoPricing") PricingStrategy promoPricing
    ) {
        this.routeInfoProvider = routeInfoProvider;
        this.standardPricing = standardPricing;
        this.surgePricing = surgePricing;
        this.promoPricing = promoPricing;
    }

    public PricingQuote execute(CalculateTripFareRequest request) {
        if (request == null || request.origin() == null || request.destination() == null) {
            throw new IllegalArgumentException("Origin and destination are required");
        }

        RouteInfoProvider.RouteInfo route = routeInfoProvider.getRoute(
            request.origin(),
            request.destination()
        );
        BigDecimal distanceFare = BigDecimal.valueOf(route.distanceMeters())
            .multiply(BigDecimal.valueOf(PriceMultiplier.PRICE_PER_METER));
        BigDecimal durationMinutes = BigDecimal.valueOf(route.durationSeconds())
            .divide(BigDecimal.valueOf(60), CALCULATION_SCALE, RoundingMode.HALF_UP);
        BigDecimal timeFare = durationMinutes
            .multiply(BigDecimal.valueOf(PriceMultiplier.PRICE_PER_MINUTE));

        BigDecimal baseFare = distanceFare.add(timeFare);
        BigDecimal surgeMultiplier = Boolean.TRUE.equals(request.surgeApplied())
            ? surgePricing.multiplier()
            : standardPricing.multiplier();
        BigDecimal promotionMultiplier = Boolean.TRUE.equals(request.promoApplied())
            ? promoPricing.multiplier()
            : standardPricing.multiplier();
        BigDecimal toll = BigDecimal.valueOf(PriceMultiplier.TOLL);
        BigDecimal taxRate = BigDecimal.valueOf(PriceMultiplier.TAX);
        BigDecimal totalFare = baseFare.multiply(surgeMultiplier)
            .add(toll)
            .multiply(promotionMultiplier)
            .multiply(BigDecimal.ONE.add(taxRate))
            .setScale(FARE_SCALE, RoundingMode.HALF_UP);

        return new PricingQuote(
            route.distanceMeters(),
            route.durationSeconds(),
            baseFare.setScale(FARE_SCALE, RoundingMode.HALF_UP),
            surgeMultiplier,
            toll,
            promotionMultiplier,
            taxRate,
            totalFare
        );
    }
}
