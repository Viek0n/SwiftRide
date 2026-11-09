package com.mircroservice.trip_service.domain.entities;

import java.time.Instant;

import com.mircroservice.trip_service.domain.enums.TripStatus;
import com.mircroservice.trip_service.domain.value_objects.Position;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter 
@Setter 
@NoArgsConstructor 
@AllArgsConstructor 
public class Trip {
    private String id;
    private Position from;
    private Position to;
    private TripStatus status;
    private Double distance;
    private int price;
    private String riderId;
    private String driverId;
    private Instant createdAt;
}
