
package com.mircroservice.trip_service.infrastructure.documents;

import java.math.BigDecimal;
import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import com.mircroservice.trip_service.domain.enums.TripStatus;
import com.mircroservice.trip_service.domain.value_objects.TripRoute;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "trips")
public class TripDocument {

    @Id
    private String id;

    private GeoJsonPoint from;

    private GeoJsonPoint to;

    private TripStatus status;

    private Double distance;

    private BigDecimal price;

    private TripRoute route;

    @Field(targetType = FieldType.OBJECT_ID)
    private String riderId;

    @Field(targetType = FieldType.OBJECT_ID)
    private String driverId;

    private Instant createdAt;
}
