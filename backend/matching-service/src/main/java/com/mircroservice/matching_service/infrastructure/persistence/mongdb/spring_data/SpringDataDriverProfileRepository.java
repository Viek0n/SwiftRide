package com.mircroservice.matching_service.infrastructure.persistence.mongdb.spring_data;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.mircroservice.matching_service.infrastructure.persistence.mongdb.documents.DriverProfileDocument;

public interface SpringDataDriverProfileRepository extends MongoRepository<DriverProfileDocument, String>{
    Optional<DriverProfileDocument> findByUserId(String userId);
    Boolean existsByUserId(String userId);
}
