package com.mircroservice.matching_service.infrastructure.persistence.mongdb.documents;

import org.springframework.data.mongodb.core.mapping.Document;

import com.mircroservice.matching_service.domain.enumerators.ActiveStatus;
import com.mongodb.client.model.geojson.Position;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter 
@Setter 
@NoArgsConstructor 
@Document ("active_drivers")
public class ActiveDriverDocument {
    private String userId;

    private ActiveStatus activeStatus;
    private Position activePosition;
}
