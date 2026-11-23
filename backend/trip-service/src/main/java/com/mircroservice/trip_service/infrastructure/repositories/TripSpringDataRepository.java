package com.mircroservice.trip_service.infrastructure.repositories;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.mircroservice.trip_service.infrastructure.documents.TripDocument;

public interface TripSpringDataRepository
        extends MongoRepository<TripDocument, String> {

    List<TripDocument> findByRiderId(String riderId);
}