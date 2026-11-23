package com.mircroservice.matching_service.infrastructure.persistence.mongdb.spring_data;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.mircroservice.matching_service.infrastructure.persistence.mongdb.documents.TripOfferDocument;

public interface SpringDataTripOfferRepository
    extends MongoRepository<TripOfferDocument, String> {
    List<TripOfferDocument> findByTripId(String tripId);

    Optional<TripOfferDocument> findByTripIdAndDriverId(String tripId, String driverId);
}
