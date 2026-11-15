package com.mircroservice.matching_service.infrastructure.persistence.mongdb.spring_data;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

import com.mircroservice.matching_service.domain.enumerators.ActiveStatus;
import com.mircroservice.matching_service.infrastructure.persistence.mongdb.documents.ActiveDriverDocument;

public interface SpringDataActiveDriverRepository extends MongoRepository<ActiveDriverDocument, String>{
    List<ActiveDriverDocument> findByActiveStatus(ActiveStatus activeStatus);
}