package com.mircroservice.matching_service.infrastructure.persistence.mongdb.spring_data;

import java.util.List;

import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;

import com.mircroservice.matching_service.domain.enumerators.ActiveStage;
import com.mircroservice.matching_service.infrastructure.persistence.mongdb.documents.ActiveDriverDocument;

public interface SpringDataActiveDriverRepository extends MongoRepository<ActiveDriverDocument, String>{
    List<ActiveDriverDocument> findByActiveStatus(ActiveStage activeStatus);

    @Query("{ '_id': ?0 }")
    @Update("{ '$set': { 'activeStatus': ?1 } }")
    long updateStatus(String userId, ActiveStage status);

    @Query("{ '_id': ?0 }")
    @Update("{ '$set': { 'activePosition': ?1 } }")
    long updatePosition(String userId, GeoJsonPoint position);
}