package com.mircroservice.matching_service.domain.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MatchingAttempt {
    private String tripId;
    private String userId;
    private String driverId;
}
