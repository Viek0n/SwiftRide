package com.mircroservice.matching_service.infrastructure.persistence.mongdb.documents;

import java.time.Instant;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.mapping.Document;

import com.mircroservice.matching_service.domain.enumerators.MatchingStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter 
@Setter 
@NoArgsConstructor 
@AllArgsConstructor 
@Document (collection = "matching_sessions")
public class MatchingSessionDocument {

    @Id 
    private String id;

    private String tripId;

    private GeoJsonPoint riderPosition;

    private int attempt;
    private int maxAttempts;

    private Set<String> excludedDriverIds;

    private String currentDriverId;
    private String currentOfferId;

    private MatchingStatus status;

    private Instant createdAt;
    private Instant expiresAt;
}
