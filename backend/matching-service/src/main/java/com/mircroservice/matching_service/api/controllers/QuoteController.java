package com.mircroservice.matching_service.api.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.mircroservice.matching_service.api.routes.QuoteRoute;
import com.mircroservice.matching_service.application.usecases.matching.CalculateTripFareUseCase;
import com.mircroservice.matching_service.domain.dto.requests.CalculateTripFareRequest;
import com.mircroservice.matching_service.domain.entities.PricingQuote;

import lombok.AllArgsConstructor;

@RestController 
@AllArgsConstructor 
public class QuoteController {
    private CalculateTripFareUseCase calculateTripFare;

    @PostMapping(QuoteRoute.GET)
    public ResponseEntity<PricingQuote> fare(@RequestBody CalculateTripFareRequest request) {
        return ResponseEntity.ok(calculateTripFare.execute(request));
    }
}
    