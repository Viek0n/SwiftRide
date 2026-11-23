package com.mircroservice.matching_service.infrastructure.persistence.mongdb.documents;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.mapping.Document;

import com.mircroservice.matching_service.domain.enumerators.ActiveStage;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter 
@Setter 
@NoArgsConstructor 
@Document ("active_drivers")
public class ActiveDriverDocument {
    @Id
    private String userId;

    private ActiveStage activeStage;
    private GeoJsonPoint activePosition;
}
