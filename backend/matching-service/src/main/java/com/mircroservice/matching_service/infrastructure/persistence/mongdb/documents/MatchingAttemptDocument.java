package com.mircroservice.matching_service.infrastructure.persistence.mongdb.documents;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Document("matching_attempts")
public class MatchingAttemptDocument {
    @Id
    private String id;
    private String tripId;
    private String userId;
    private String driverId;
}
