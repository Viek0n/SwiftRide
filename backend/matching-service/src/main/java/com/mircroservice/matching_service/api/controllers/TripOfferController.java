package com.mircroservice.matching_service.api.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mircroservice.matching_service.application.usecases.matching.TripOfferAcceptedUseCase;
import com.mircroservice.matching_service.application.usecases.matching.TripOfferRejectedUseCase;
import com.mircroservice.matching_service.domain.entities.TripOffer;
import com.mircroservice.matching_service.domain.repositories.TripOfferRepository;

@RestController
@RequestMapping("api/trips-offer")
public class TripOfferController {
    private final TripOfferRepository offerRepository;
    private final TripOfferAcceptedUseCase acceptedUseCase;
    private final TripOfferRejectedUseCase rejectedUseCase;

    public TripOfferController(
            TripOfferRepository offerRepository,
            TripOfferAcceptedUseCase acceptedUseCase,
            TripOfferRejectedUseCase rejectedUseCase) {
        this.offerRepository = offerRepository;
        this.acceptedUseCase = acceptedUseCase;
        this.rejectedUseCase = rejectedUseCase;
    }

    @GetMapping("/me/offers")
    public List<TripOffer> getMyOffers(
            @RequestHeader(value = "X-Driver-Id", required = false) String headerDriverId,
            @RequestParam(value = "driverId", required = false) String paramDriverId) {
        String driverId = resolveDriverId(headerDriverId, paramDriverId);
        return offerRepository.getAllBydDriverId(driverId).stream()
                .filter(TripOffer::isPending)
                .toList();
    }

    @PostMapping("/{tripId}/driver-reject")
    public ResponseEntity<TripOffer> reject(
            @PathVariable String tripId,
            @RequestHeader(value = "X-Driver-Id", required = false) String headerDriverId,
            @RequestParam(value = "driverId", required = false) String paramDriverId) {
        String driverId = resolveDriverId(headerDriverId, paramDriverId);
        TripOffer offer = offerRepository.findByTripId(tripId)
                .orElseThrow(() -> new IllegalArgumentException("Offer not found"));
        rejectedUseCase.execute(offer.getId(), driverId);
        return ResponseEntity.ok(offerRepository.findById(offer.getId()).orElse(offer));
    }

    @PostMapping("/{tripId}/driver-accept")
    public ResponseEntity<TripOffer> accept(
            @PathVariable String tripId,
            @RequestHeader(value = "X-Driver-Id", required = false) String headerDriverId,
            @RequestParam(value = "driverId", required = false) String paramDriverId) {
        String driverId = resolveDriverId(headerDriverId, paramDriverId);
        TripOffer offer = offerRepository.findByTripId(tripId)
                .orElseThrow(() -> new IllegalArgumentException("Offer not found"));
        acceptedUseCase.execute(offer.getId(), driverId);
        return ResponseEntity.ok(offerRepository.findById(offer.getId()).orElse(offer));
    }

    private String resolveDriverId(String headerDriverId, String paramDriverId) {
        String driverId = headerDriverId != null ? headerDriverId : paramDriverId;
        if (driverId == null || driverId.isBlank()) {
            throw new IllegalArgumentException("Authenticated driver id is required");
        }
        return driverId;
    }
}
