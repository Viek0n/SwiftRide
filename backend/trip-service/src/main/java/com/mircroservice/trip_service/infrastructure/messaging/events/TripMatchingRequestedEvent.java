package com.mircroservice.trip_service.infrastructure.messaging.events;

import com.mircroservice.trip_service.domain.value_objects.Position;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter 
@Setter 
@AllArgsConstructor 
@NoArgsConstructor 
public class TripMatchingRequestedEvent {
    private String tripId;
    private String riderId;
    private Position from;
    private Position to;
}
