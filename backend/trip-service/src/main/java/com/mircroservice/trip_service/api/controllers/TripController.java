package com.mircroservice.trip_service.api.controllers;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mircroservice.trip_service.application.dto.DriverAssignmentRequest;
import com.mircroservice.trip_service.application.dto.TripCreateRequest;
import com.mircroservice.trip_service.domain.entities.Trip;
import com.mircroservice.trip_service.infrastructure.services.TripService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/trips")
public class TripController {
    private final TripService tripService;

    public TripController(TripService tripService) {
        this.tripService = tripService;
    }

    @PostMapping
    public ResponseEntity<Trip> createTrip(@Valid @RequestBody TripCreateRequest request) {
        var trip = tripService.createTrip(request);
        return ResponseEntity.created(URI.create("/api/trips/" + trip.getId())).body(trip);
    }

    @GetMapping("/{tripId}")
    public Trip getTrip(@PathVariable String tripId) {
        return tripService.getTrip(tripId);
    }

    @GetMapping
    public List<Trip> getTripsForRider(@RequestParam String riderId) {
        return tripService.getTripsForRider(riderId);
    }

    @PatchMapping("/{tripId}/accept")
    public Trip acceptTrip(
        @PathVariable String tripId,
        @Valid @RequestBody DriverAssignmentRequest request
    ) {
        return tripService.assignDriver(tripId, request.driverId());
    }

    @PatchMapping("/{tripId}/pickup")
    public Trip markPickedUp(@PathVariable String tripId) {
        return tripService.markPickedUp(tripId);
    }

    @PatchMapping("/{tripId}/complete")
    public Trip completeTrip(@PathVariable String tripId) {
        return tripService.completeTrip(tripId);
    }

    @PatchMapping("/{tripId}/cancel")
    public Trip cancelTrip(@PathVariable String tripId) {
        return tripService.cancelTrip(tripId);
    }

    @PatchMapping("/{tripId}/matching-failed")
    public Trip markMatchingFailed(@PathVariable String tripId) {
        return tripService.markMatchingFailed(tripId);
    }
}
