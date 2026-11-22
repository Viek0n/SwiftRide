package com.mircroservice.trip_service.domain.repositories;

import java.util.List;
import java.util.Optional;

import com.mircroservice.trip_service.domain.entities.OutboxEvent;
import com.mircroservice.trip_service.infrastructure.documents.OutboxDocument;

public interface OutboxRepository {
    Optional<OutboxDocument> findById(String id);
    OutboxEvent save(OutboxDocument event);
    List<OutboxDocument> findTop100ByStatusOrderByCreatedAtAsc(String status);
}
