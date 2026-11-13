package com.mircroservice.matching_service.infrastructure.persistence.mongdb.spring_data;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.mircroservice.matching_service.infrastructure.persistence.mongdb.documents.ActiveDriverDocument;

public interface SpringDataActiveDriverRepository extends MongoRepository<ActiveDriverDocument, String>{

    
}