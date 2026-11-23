package com.mircroservice.matching_service.application.usecases.matching;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.mircroservice.matching_service.application.abstractions.RouteInfoProvider;
import com.mircroservice.matching_service.domain.constants.PriceMultiplier;
import com.mircroservice.matching_service.domain.dto.requests.CalculateTripFareRequest;
import com.mircroservice.matching_service.domain.strategies.PromoStrategy;
import com.mircroservice.matching_service.domain.strategies.StandartPricing;
import com.mircroservice.matching_service.domain.strategies.SurgePricing;
import com.mircroservice.matching_service.domain.value_objects.Position;

@ExtendWith(MockitoExtension.class)
class CalculateTripFareUseCaseTest {
    @Mock
    private RouteInfoProvider routeInfoProvider;

    private CalculateTripFareUseCase calculateTripFare;

    @Test
    void appliesConfiguredPromoMultiplierInsteadOfZeroingTheFare() {
        Position origin = new Position(0.0, 0.0);
        Position destination = new Position(1.0, 1.0);
        when(routeInfoProvider.getRoute(origin, destination))
            .thenReturn(new RouteInfoProvider.RouteInfo(1000, 600));

        calculateTripFare = new CalculateTripFareUseCase(
            routeInfoProvider,
            new StandartPricing(),
            new SurgePricing(),
            new PromoStrategy()
        );

        var quote = calculateTripFare.execute(
            new CalculateTripFareRequest(origin, destination, false, true)
        );

        BigDecimal baseFare = BigDecimal.valueOf(1000)
            .multiply(BigDecimal.valueOf(PriceMultiplier.PRICE_PER_METER))
            .add(BigDecimal.valueOf(10)
                .multiply(BigDecimal.valueOf(PriceMultiplier.PRICE_PER_MINUTE)));
        BigDecimal expectedFare = baseFare
            .add(BigDecimal.valueOf(PriceMultiplier.TOLL))
            .multiply(BigDecimal.valueOf(PriceMultiplier.PROMO))
            .multiply(BigDecimal.ONE.add(BigDecimal.valueOf(PriceMultiplier.TAX)))
            .setScale(2, RoundingMode.HALF_UP);

        assertThat(quote.promotionMultiplier())
            .isEqualByComparingTo(BigDecimal.valueOf(PriceMultiplier.PROMO));
        assertThat(quote.totalFare()).isEqualByComparingTo(expectedFare);
        assertThat(quote.totalFare()).isNotZero();
    }
}
