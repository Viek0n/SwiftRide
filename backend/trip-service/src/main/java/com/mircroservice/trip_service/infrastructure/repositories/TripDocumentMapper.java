
package com.mircroservice.trip_service.infrastructure.repositories;

import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.stereotype.Component;

import com.mircroservice.trip_service.domain.entities.Trip;
import com.mircroservice.trip_service.domain.value_objects.Position;
import com.mircroservice.trip_service.infrastructure.documents.TripDocument;

@Component
public class TripDocumentMapper {

    public TripDocument toDocument(Trip trip) {
        return TripDocument.builder()
                .id(trip.getId())
                .from(toGeoJson(trip.getFrom()))
                .to(toGeoJson(trip.getTo()))
                .status(trip.getStatus())
                .distance(trip.getDistance())
                .price(trip.getPrice())
                .route(trip.getRoute())
                .riderId(trip.getRiderId())
                .driverId(trip.getDriverId())
                .createdAt(trip.getCreatedAt())
                .build();
    }

    public Trip toDomain(TripDocument document) {
        Trip trip = new Trip(
                document.getId(),
                toPosition(document.getFrom()),
                toPosition(document.getTo()),
                document.getRiderId(),
                document.getRoute(),
                document.getPrice(),
                document.getCreatedAt()
        );

        // Restore the persisted state.
        // This requires a restoration constructor or factory in Trip.
        trip.restoreState(
                document.getStatus(),
                document.getDriverId()
        );

        return trip;
    }

    private GeoJsonPoint toGeoJson(Position position) {
        if (position == null) {
            return null;
        }

        return new GeoJsonPoint(
                position.longitude(),
                position.latitude()
        );
    }

    private Position toPosition(GeoJsonPoint point) {
        if (point == null) {
            return null;
        }

        return new Position(
                point.getY(),
                point.getX()
        );
    }
}
