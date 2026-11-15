package com.mircroservice.trip_service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
import com.mircroservice.trip_service.infrastructure.services.TripService;

class TripLifecycleTests {
    @Test
    void createsAndCompletesTripInOrder() {
        var repository = new InMemoryTripRepository();
        var service = new TripService(repository);

        var created = service.createTrip(new TripCreateRequest(
            "rider-1",
            new Position(10.0, 106.0),
            new Position(10.1, 106.1)
        ));

        assertEquals(TripStatus.MATCHING, created.getStatus());
        assertNull(created.getPrice());

        var assigned = service.assignDriver(created.getId(), "driver-1");
        assertEquals(TripStatus.PICKUP, assigned.getStatus());
        assertEquals("driver-1", assigned.getDriverId());

        assertEquals(TripStatus.INPROGRESS, service.markPickedUp(created.getId()).getStatus());
        assertEquals(TripStatus.COMPLETED, service.completeTrip(created.getId()).getStatus());
        assertThrows(IllegalStateException.class, () -> service.cancelTrip(created.getId()));
    }

    @Test
    void rejectsPickupBeforeDriverIsAssigned() {
        var service = new TripService(new InMemoryTripRepository());
        var created = service.createTrip(new TripCreateRequest(
            "rider-1",
            new Position(10.0, 106.0),
            new Position(10.1, 106.1)
        ));

        assertThrows(IllegalStateException.class, () -> service.markPickedUp(created.getId()));
    }

    @Test
    void matchingCanFailOrBeCancelledButTerminalStatesCannotAdvance() {
        var service = new TripService(new InMemoryTripRepository());
        var failedTrip = service.createTrip(new TripCreateRequest(
            "rider-1",
            new Position(10.0, 106.0),
            new Position(10.1, 106.1)
        ));
        var cancelledTrip = service.createTrip(new TripCreateRequest(
            "rider-2",
            new Position(10.0, 106.0),
            new Position(10.1, 106.1)
        ));

        assertEquals(TripStatus.MATCHINGFAIL, service.markMatchingFailed(failedTrip.getId()).getStatus());
        assertEquals(TripStatus.CANCELLED, service.cancelTrip(cancelledTrip.getId()).getStatus());
        assertThrows(IllegalStateException.class, () -> service.assignDriver(failedTrip.getId(), "driver-1"));
        assertThrows(IllegalStateException.class, () -> service.assignDriver(cancelledTrip.getId(), "driver-1"));
    }

    @Test
    void cannotReassignDriverAfterTripHasBeenAccepted() {
        var service = new TripService(new InMemoryTripRepository());
        var created = service.createTrip(new TripCreateRequest(
            "rider-1",
            new Position(10.0, 106.0),
            new Position(10.1, 106.1)
        ));
        service.assignDriver(created.getId(), "driver-1");

        assertThrows(IllegalStateException.class, () -> service.assignDriver(created.getId(), "driver-2"));
        assertEquals("driver-1", service.getTrip(created.getId()).getDriverId());
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
