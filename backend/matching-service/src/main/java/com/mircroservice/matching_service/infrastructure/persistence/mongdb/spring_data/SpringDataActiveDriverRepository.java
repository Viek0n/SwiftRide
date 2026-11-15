package com.mircroservice.matching_service.infrastructure.persistence.mongdb.spring_data;

import java.util.List;

import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;

import com.mircroservice.matching_service.domain.enumerators.ActiveStatus;
import com.mircroservice.matching_service.infrastructure.persistence.mongdb.documents.ActiveDriverDocument;

public interface SpringDataActiveDriverRepository extends MongoRepository<ActiveDriverDocument, String>{
    List<ActiveDriverDocument> findByActiveStatus(ActiveStatus activeStatus);

    @Query("{ '_id': ?0, 'activeStatus': ?1 }")
    @Update("{ '$set': { 'activeStatus': ?2 } }")
    long updateStatusIfCurrent(String userId, ActiveStatus currentStatus, ActiveStatus nextStatus);

    @Query("{ '_id': ?0, 'activeStatus': ?1 }")
    @Update("{ '$set': { 'activeStatus': 'ONLINE', 'activePosition': ?2 } }")
    long updatePositionIfCurrent(String userId, ActiveStatus currentStatus, GeoJsonPoint position);
}