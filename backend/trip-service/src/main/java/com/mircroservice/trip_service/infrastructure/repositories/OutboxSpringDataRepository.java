package com.mircroservice.trip_service.infrastructure.repositories;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.mircroservice.trip_service.infrastructure.documents.OutboxDocument;

public interface OutboxSpringDataRepository extends MongoRepository<OutboxDocument, String> {
    List<OutboxDocument> findTop100ByStatusOrderByCreatedAtAsc(String status);
}
