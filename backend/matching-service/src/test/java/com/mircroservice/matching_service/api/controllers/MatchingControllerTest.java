package com.mircroservice.matching_service.api.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.mircroservice.matching_service.application.abstractions.RouteInfoProvider;
import com.mircroservice.matching_service.application.usecases.CalculateTripFareUseCase;
import com.mircroservice.matching_service.application.usecases.GetRouteUseCase;
import com.mircroservice.matching_service.application.usecases.MatchingRideUseCase;
import com.mircroservice.matching_service.domain.entities.MatchingAttempt;
import com.mircroservice.matching_service.domain.entities.PricingQuote;
import com.mircroservice.matching_service.domain.value_objects.Position;

class MatchingControllerTest {
    private final GetRouteUseCase getRoute = mock(GetRouteUseCase.class);
    private final CalculateTripFareUseCase calculateTripFare = mock(CalculateTripFareUseCase.class);
    private final MatchingRideUseCase matchingRide = mock(MatchingRideUseCase.class);
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new MatchingController(
            getRoute,
            calculateTripFare,
            matchingRide
        )).build();
    }

    @Test
    void returnsMapLibreCompatibleRouteGeometry() throws Exception {
        when(getRoute.execute(any(Position.class), any(Position.class))).thenReturn(
            new RouteInfoProvider.RouteInfo(
                1_000,
                120,
                List.of(List.of(106.0, 10.0), List.of(106.01, 10.01))
            )
        );

        mockMvc.perform(post("/match/route")
                .contentType("application/json")
                .content("""
                    {"origin":{"lat":10.0,"lng":106.0},
                     "destination":{"lat":10.01,"lng":106.01}}
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.distanceMeters").value(1000))
            .andExpect(jsonPath("$.geometry.type").value("LineString"))
            .andExpect(jsonPath("$.geometry.coordinates[0][0]").value(106.0));
    }

    @Test
    void returnsFareCalculatedForOriginAndDestination() throws Exception {
        when(calculateTripFare.execute(any())).thenReturn(new PricingQuote(
            1_000,
            120,
            new BigDecimal("5004.00"),
            BigDecimal.ONE,
            new BigDecimal("10"),
            BigDecimal.ONE,
            new BigDecimal("0.01"),
            new BigDecimal("5064.14")
        ));

        mockMvc.perform(post("/match/fare")
                .contentType("application/json")
                .content("""
                    {"origin":{"lat":10.0,"lng":106.0},
                     "destination":{"lat":10.01,"lng":106.01}}
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.totalFare").value(5064.14));
    }

    @Test
    void matchReturnsTheDriverCurrentlyBeingAsked() throws Exception {
        Position origin = new Position(10.0, 106.0);
        when(matchingRide.start("trip-1", "rider-1", origin))
            .thenReturn(new MatchingAttempt("trip-1", "rider-1", "driver-1"));
        when(matchingRide.getAttemptedDriverCount("trip-1")).thenReturn(1);

        mockMvc.perform(post("/match")
                .contentType("application/json")
                .content("""
                    {"tripId":"trip-1","userId":"rider-1","origin":{"lat":10.0,"lng":106.0}}
                    """))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.attemptedDrivers").value(1))
            .andExpect(jsonPath("$.tripId").value("trip-1"))
            .andExpect(jsonPath("$.userId").value("rider-1"))
            .andExpect(jsonPath("$.driverId").value("driver-1"));
    }

}
