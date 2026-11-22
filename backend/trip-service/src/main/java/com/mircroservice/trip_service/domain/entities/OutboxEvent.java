package com.mircroservice.trip_service.domain.entities;

import java.time.Instant;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter 
@Setter 
@NoArgsConstructor 
@AllArgsConstructor 
public class OutboxEvent {
    private String id;

    private String eventType;

    private String aggregateId;

    private Object payload;

    private Instant publishedAt;

    private String status;
}
