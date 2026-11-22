package com.mircroservice.matching_service.api.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mircroservice.matching_service.api.routes.MatchingRoute;
import com.mircroservice.matching_service.api.routes.QuoteRoute;
import com.mircroservice.matching_service.application.abstractions.RouteInfoProvider;
import com.mircroservice.matching_service.application.usecases.matching.CalculateTripFareUseCase;
import com.mircroservice.matching_service.application.usecases.matching.GetRouteUseCase;
import com.mircroservice.matching_service.domain.dto.requests.CalculateTripFareRequest;
import com.mircroservice.matching_service.domain.dto.requests.RouteRequest;
import com.mircroservice.matching_service.domain.dto.responses.RouteResponse;
import com.mircroservice.matching_service.domain.entities.PricingQuote;

import lombok.AllArgsConstructor;

@RestController  
@RequestMapping(MatchingRoute.BASE)
@AllArgsConstructor 
public class MatchingController {
    private final GetRouteUseCase getRoute;
    private CalculateTripFareUseCase calculateTripFare;

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

        
    @PostMapping(MatchingRoute.QUOTE)
    public ResponseEntity<PricingQuote> fare(@RequestBody CalculateTripFareRequest request) {
        return ResponseEntity.ok(calculateTripFare.execute(request));
    }
}
