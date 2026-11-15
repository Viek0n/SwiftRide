package com.mircroservice.matching_service.application.usecases;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import com.mircroservice.matching_service.application.abstractions.RouteInfoProvider;
import com.mircroservice.matching_service.domain.dto.requests.CalculateTripFareRequest;
import com.mircroservice.matching_service.domain.strategies.PromoStrategy;
import com.mircroservice.matching_service.domain.strategies.StandartPricing;
import com.mircroservice.matching_service.domain.strategies.SurgePricing;
import com.mircroservice.matching_service.domain.value_objects.Position;

class CalculateTripFareUseCaseTest {
    private final RouteInfoProvider routeInfoProvider =
        (origin, destination) -> new RouteInfoProvider.RouteInfo(1_000, 120);
    private final CalculateTripFareUseCase useCase = new CalculateTripFareUseCase(
        routeInfoProvider,
        new StandartPricing(),
        new SurgePricing(),
        new PromoStrategy()
    );

    @Test
    void calculatesFareFromOsrmDistanceAndDuration() {
        var quote = useCase.execute(request(false, false));

        assertEquals(new BigDecimal("5004.00"), quote.baseFare());
        assertEquals(new BigDecimal("10"), quote.toll());
        assertEquals(new BigDecimal("5064.14"), quote.totalFare());
    }

    @Test
    void appliesSurgeBeforeTollAndTax() {
        var quote = useCase.execute(request(true, true));

        assertEquals(new BigDecimal("1.1"), quote.surgeMultiplier());
        assertEquals(new BigDecimal("1.0"), quote.promotionMultiplier());
        assertEquals(new BigDecimal("5569.54"), quote.totalFare());
    }

    private CalculateTripFareRequest request(boolean surgeApplied, boolean promoApplied) {
        return new CalculateTripFareRequest(
            new Position(10.0, 106.0),
            new Position(10.01, 106.01),
            surgeApplied,
            promoApplied
        );
    }
}
