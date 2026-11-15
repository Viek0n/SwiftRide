package com.mircroservice.matching_service.api.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mircroservice.matching_service.api.routes.MatchingRoute;
import com.mircroservice.matching_service.application.abstractions.RouteInfoProvider;
import com.mircroservice.matching_service.application.usecases.CalculateTripFareUseCase;
import com.mircroservice.matching_service.application.usecases.GetRouteUseCase;
import com.mircroservice.matching_service.application.usecases.MatchingRideUseCase;
import com.mircroservice.matching_service.domain.dto.requests.CalculateTripFareRequest;
import com.mircroservice.matching_service.domain.dto.requests.MatchDecisionRequest;
import com.mircroservice.matching_service.domain.dto.requests.MatchRequest;
import com.mircroservice.matching_service.domain.dto.requests.RouteRequest;
import com.mircroservice.matching_service.domain.dto.responses.MatchingResponse;
import com.mircroservice.matching_service.domain.dto.responses.RouteResponse;
import com.mircroservice.matching_service.domain.entities.MatchingAttempt;
import com.mircroservice.matching_service.domain.entities.PricingQuote;

@RestController  
@RequestMapping(MatchingRoute.BASE)
public class MatchingController {
    private final GetRouteUseCase getRoute;
    private final CalculateTripFareUseCase calculateTripFare;
    private final MatchingRideUseCase matchingRide;

    public MatchingController(
        GetRouteUseCase getRoute,
        CalculateTripFareUseCase calculateTripFare,
        MatchingRideUseCase matchingRide
    ) {
        this.getRoute = getRoute;
        this.calculateTripFare = calculateTripFare;
        this.matchingRide = matchingRide;
    }

    @PostMapping(MatchingRoute.ROUTE)
    public ResponseEntity<RouteResponse> route(@RequestBody RouteRequest request) {
        RouteInfoProvider.RouteInfo route = getRoute.execute(request.origin(), request.destination());
        RouteResponse response = new RouteResponse(
            route.distanceMeters(),
            route.durationSeconds(),
            new RouteResponse.Geometry("LineString", route.geometryCoordinates())
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping(MatchingRoute.CALCULATE)
    public ResponseEntity<PricingQuote> fare(@RequestBody CalculateTripFareRequest request) {
        return ResponseEntity.ok(calculateTripFare.execute(request));
    }

    @PostMapping
    public ResponseEntity<MatchingResponse> match(@RequestBody MatchRequest request) {
        return matchingRide.start(request.tripId(), request.userId(), request.origin())
            .map(this::toResponse)
            .map(response -> ResponseEntity.status(HttpStatus.CREATED).body(response))
            .orElseGet(() -> ResponseEntity.noContent().build());
    }

    @PostMapping(MatchingRoute.ACCEPT)
    public ResponseEntity<MatchingResponse> accept(@RequestBody MatchDecisionRequest request) {
        return ResponseEntity.ok(toResponse(
            matchingRide.accept(request.tripId(), request.driverId())
        ));
    }

    @PostMapping(MatchingRoute.DECLINE)
    public ResponseEntity<MatchingResponse> decline(@RequestBody MatchDecisionRequest request) {
        return ResponseEntity.ok(toResponse(
            matchingRide.decline(request.tripId(), request.driverId())
        ));
    }

    @PostMapping(MatchingRoute.CONFIRM)
    public ResponseEntity<MatchingResponse> confirm(@RequestBody MatchDecisionRequest request) {
        return ResponseEntity.ok(toResponse(
            matchingRide.confirm(request.tripId(), request.driverId())
        ));
    }

    private MatchingResponse toResponse(MatchingAttempt attempt) {
        return MatchingResponse.toResponse(
            attempt,
            matchingRide.getAttemptedDriverCount(attempt.getTripId())
        );
    }
}
