package com.mircroservice.trip_service.infrastructure.documents;

import java.time.Instant;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Document (collection = "outbox")
@Getter 
@Setter 
@Builder 
@NoArgsConstructor 
@AllArgsConstructor 
public class OutboxDocument {
    @Id
    private String id;

    private String aggregateType;

    private String aggregateId;

    private String eventType;

    private Map<String, Object> payload;

    private String status;

    private Instant createdAt;

    private Instant publishedAt;

    private Integer retryCount;
}
