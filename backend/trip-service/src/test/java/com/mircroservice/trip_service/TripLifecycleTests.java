package com.mircroservice.trip_service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.mircroservice.trip_service.application.dto.TripCreateRequest;
import com.mircroservice.trip_service.domain.entities.Trip;
import com.mircroservice.trip_service.domain.enums.TripStatus;
import com.mircroservice.trip_service.domain.repositories.TripRepository;
import com.mircroservice.trip_service.domain.value_objects.Position;
import com.mircroservice.trip_service.infrastructure.clients.MatchingServiceClient;
import com.mircroservice.trip_service.infrastructure.services.TripService;

class TripLifecycleTests {
    private static final Position ORIGIN = new Position(10.0, 106.0);
    private static final Position DESTINATION = new Position(10.1, 106.1);

    @Test
    void createsRequestedTripWithRouteAndFareThenMatchesAfterRiderConfirmation() {
        var repository = new InMemoryTripRepository();
        var matchingService = matchingServiceClient();
        when(matchingService.match(anyString(), eq("rider-1"), eq(ORIGIN))).thenReturn(
            Optional.of(match("trip-1", "rider-1", "driver-1"))
        );
        var service = new TripService(repository, matchingService);

        var created = service.createTrip(request("rider-1"));

        assertEquals(TripStatus.REQUESTED, created.getStatus());
        assertEquals(new BigDecimal("5064.14"), created.getPrice());
        assertEquals(1_000.0, created.getDistance());
        assertEquals("LineString", created.getRoute().geometry().type());
        assertEquals(List.of(106.0, 10.0), created.getRoute().geometry().coordinates().get(0));
        verify(matchingService).getRoute(ORIGIN, DESTINATION);
        verify(matchingService).calculateFare(ORIGIN, DESTINATION);
        verify(matchingService, never()).match(anyString(), anyString(), any(Position.class));

        var matching = service.confirmTrip(created.getId());
        assertEquals(TripStatus.MATCHING, matching.getStatus());
        assertEquals("driver-1", matching.getDriverId());

        var assigned = service.assignDriver(created.getId(), "driver-1");
        assertEquals(TripStatus.INPROGRESS, assigned.getStatus());
        assertEquals("driver-1", assigned.getDriverId());
        verify(matchingService).accept(created.getId(), "driver-1");
        verify(matchingService).confirm(created.getId(), "driver-1");

        assertThrows(
            IllegalArgumentException.class,
            () -> service.completeTrip(created.getId(), "driver-2")
        );
        assertEquals(
            TripStatus.COMPLETE,
            service.completeTrip(created.getId(), "driver-1").getStatus()
        );
        assertThrows(IllegalStateException.class, () -> service.cancelTrip(created.getId()));
    }

    @Test
    void rejectsCompletionBeforeDriverIsAssigned() {
        var service = new TripService(new InMemoryTripRepository(), matchingServiceClient());
        var created = service.createTrip(request("rider-1"));

        assertThrows(
            IllegalStateException.class,
            () -> service.completeTrip(created.getId(), "driver-1")
        );
    }

    @Test
    void marksTripFailedWhenNoDriverIsAvailableAndCanCancelUnconfirmedTrip() {
        var matchingService = matchingServiceClient();
        when(matchingService.match(anyString(), anyString(), any(Position.class)))
            .thenReturn(Optional.empty());
        var service = new TripService(new InMemoryTripRepository(), matchingService);
        var failedTrip = service.createTrip(request("rider-1"));
        var cancelledTrip = service.createTrip(request("rider-2"));

        assertEquals(TripStatus.MATCHINGFAIL, service.confirmTrip(failedTrip.getId()).getStatus());
        assertEquals(TripStatus.CANCELLED, service.cancelTrip(cancelledTrip.getId()).getStatus());
        assertThrows(IllegalStateException.class, () -> service.assignDriver(failedTrip.getId(), "driver-1"));
        assertThrows(IllegalStateException.class, () -> service.assignDriver(cancelledTrip.getId(), "driver-1"));
    }

    @Test
    void rejectingCandidateRequestsAnotherDriverForTheSameTrip() {
        var matchingService = matchingServiceClient();
        when(matchingService.match(anyString(), eq("rider-1"), eq(ORIGIN))).thenReturn(
            Optional.of(match("trip-1", "rider-1", "driver-1")),
            Optional.of(match("trip-1", "rider-1", "driver-2"))
        );
        var service = new TripService(new InMemoryTripRepository(), matchingService);
        var requested = service.createTrip(request("rider-1"));

        var matching = service.confirmTrip(requested.getId());
        var retried = service.rejectDriver(matching.getId(), "driver-1");
        assertEquals(TripStatus.MATCHING, retried.getStatus());
        assertEquals("driver-2", retried.getDriverId());
        verify(matchingService).decline(requested.getId(), "driver-1");

        var assigned = service.assignDriver(retried.getId(), "driver-2");
        assertEquals(TripStatus.INPROGRESS, assigned.getStatus());
        assertThrows(
            IllegalStateException.class,
            () -> service.assignDriver(retried.getId(), "driver-1")
        );
    }

    private TripCreateRequest request(String riderId) {
        return new TripCreateRequest(riderId, ORIGIN, DESTINATION);
    }

    private MatchingServiceClient matchingServiceClient() {
        var matchingService = mock(MatchingServiceClient.class);
        when(matchingService.getRoute(any(Position.class), any(Position.class))).thenReturn(
            new MatchingServiceClient.RouteResult(
                1_000,
                120,
                new MatchingServiceClient.RouteResult.Geometry(
                    "LineString",
                    List.of(List.of(106.0, 10.0), List.of(106.1, 10.1))
                )
            )
        );
        when(matchingService.calculateFare(any(Position.class), any(Position.class))).thenReturn(
            new MatchingServiceClient.FareResult(new BigDecimal("5064.14"))
        );
        return matchingService;
    }

    private MatchingServiceClient.MatchResult match(String tripId, String riderId, String driverId) {
        return new MatchingServiceClient.MatchResult(tripId, riderId, driverId, 1);
    }

    private static class InMemoryTripRepository implements TripRepository {
        private final Map<String, Trip> trips = new HashMap<>();

        @Override
        public Trip save(Trip trip) {
            trips.put(trip.getId(), trip);
            return trip;
        }

        @Override
        public Optional<Trip> findById(String id) {
            return Optional.ofNullable(trips.get(id));
        }

        @Override
        public List<Trip> findByRiderId(String riderId) {
            return trips.values().stream()
                .filter(trip -> trip.getRiderId().equals(riderId))
                .toList();
        }
    }
}
