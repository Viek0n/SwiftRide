package com.mircroservice.matching_service.infrastructure.persistence.mongdb.spring_data;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;

import com.mircroservice.matching_service.domain.enumerators.ActiveStage;
import com.mircroservice.matching_service.infrastructure.persistence.mongdb.documents.ActiveDriverDocument;

public interface SpringDataActiveDriverRepository extends MongoRepository<ActiveDriverDocument, String>{
    List<ActiveDriverDocument> findByActiveStage(ActiveStage activeStage);

    Boolean existsByUserId(String userId);

    Optional<ActiveDriverDocument> findByUserId(String userId);
    
    @Query("{ 'userId': ?0 }")
    @Update("{ '$set': { 'activeStage': ?1 } }")
    long updateStatus(String userId, ActiveStage status);

    @Query("{ 'userId': ?0 }")
    @Update("{ '$set': { 'activePosition': ?1 } }")
    long updatePosition(String userId, GeoJsonPoint position);
}