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
import com.mircroservice.trip_service.api.TripRoute;

import jakarta.validation.Valid;

@RestController
@RequestMapping(TripRoute.BASE)
public class TripController {
    private final TripService tripService;

    public TripController(TripService tripService) {
        this.tripService = tripService;
    }

    @PostMapping
    public ResponseEntity<Trip> createTrip(@Valid @RequestBody TripCreateRequest request) {
        var trip = tripService.createTrip(request);
        return ResponseEntity.created(URI.create("trips/" + trip.getId())).body(trip);
    }

    @GetMapping(TripRoute.GET)
    public Trip getTrip(@PathVariable("tripId") String tripId) {
        return tripService.getTrip(tripId);
    }

    @GetMapping
    public List<Trip> getTripsForRider(@RequestParam String riderId) {
        return tripService.getTripsForRider(riderId);
    }

    @PatchMapping("/{tripId}/confirm")
    public Trip confirmTrip(@PathVariable("tripId") String tripId) {
        return tripService.confirmTrip(tripId);
    }

    @PostMapping(TripRoute.REJECT)
    public ResponseEntity<Void> rejectTrip(@PathVariable("tripId") String tripId) {
        tripService.rejectTrip(tripId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(TripRoute.PICKEDUP)
    public Trip pickedUp(
        @PathVariable("tripId") String tripId,
        @Valid @RequestBody DriverAssignmentRequest request
    ) {
        return tripService.pickedUp(tripId, request.driverId());
    }

    @PostMapping(TripRoute.COMPLETE)
    public Trip complete(
        @PathVariable("tripId") String tripId,
        @Valid @RequestBody DriverAssignmentRequest request
    ) {
        return tripService.completeTrip(tripId, request.driverId());
    }

    @PatchMapping("/{tripId}/driver-accept")
    public Trip acceptTrip(
        @PathVariable("tripId") String tripId,
        @Valid @RequestBody DriverAssignmentRequest request
    ) {
        return tripService.assignDriver(tripId, request.driverId());
    }

    @PatchMapping("/{tripId}/reject")
    public Trip rejectDriver(
        @PathVariable("tripId") String tripId,
        @Valid @RequestBody DriverAssignmentRequest request
    ) {
        return tripService.rejectDriver(tripId, request.driverId());
    }

    @PatchMapping("/{tripId}/dropoff")
    public Trip completeTrip(
        @PathVariable("tripId") String tripId,
        @Valid @RequestBody DriverAssignmentRequest request
    ) {
        return tripService.completeTrip(tripId, request.driverId());
    }

    @PatchMapping("/{tripId}/cancel")
    public Trip cancelTrip(@PathVariable("tripId") String tripId) {
        return tripService.cancelTrip(tripId);
    }

    @PatchMapping("/{tripId}/matching-failed")
    public Trip markMatchingFailed(@PathVariable("tripId") String tripId) {
        return tripService.markMatchingFailed(tripId);
    }
}
