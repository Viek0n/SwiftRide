package com.mircroservice.matching_service.infrastructure.persistence.mongdb.documents;

import java.time.Instant;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.mircroservice.matching_service.domain.enumerators.OfferStatus;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Document("trip_offers")
public class TripOfferDocument {
    @Id
    private String id;

    private String tripId;
    private String driverId;
    
    private OfferStatus status;

    private Instant createdAt;
    private Instant expiresAt;
}
