package com.mircroservice.trip_service.infrastructure.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.mircroservice.trip_service.domain.entities.OutboxEvent;
import com.mircroservice.trip_service.domain.repositories.OutboxRepository;
import com.mircroservice.trip_service.infrastructure.documents.OutboxDocument;

import lombok.AllArgsConstructor;

@AllArgsConstructor 
@Repository 
public class OutboxMongoRepository implements OutboxRepository{
    private OutboxDocumentMapper mapper;
    private OutboxSpringDataRepository mongoRepository;

    @Override
    public Optional<OutboxDocument> findById(String id) {
        return mongoRepository.findById(id);
    }

    @Override
    public OutboxEvent save(OutboxDocument document) {
        OutboxDocument saved = mongoRepository.save(document);
        return mapper.toDomain(saved);
    }

    @Override
    public List<OutboxDocument> findTop100ByStatusOrderByCreatedAtAsc(String status) {
        return mongoRepository.findTop100ByStatusOrderByCreatedAtAsc(status);
    }
    
}
