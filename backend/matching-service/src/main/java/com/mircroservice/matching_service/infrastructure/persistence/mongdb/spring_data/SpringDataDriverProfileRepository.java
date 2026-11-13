package com.mircroservice.matching_service.infrastructure.persistence.mongdb.spring_data;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.mircroservice.matching_service.infrastructure.persistence.mongdb.documents.DriverProfileDocument;

public interface SpringDataDriverProfileRepository extends MongoRepository<DriverProfileDocument, String>{

    
}
