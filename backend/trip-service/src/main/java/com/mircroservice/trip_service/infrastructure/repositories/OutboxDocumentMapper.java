package com.mircroservice.trip_service.infrastructure.repositories;

import org.springframework.stereotype.Component;

import com.mircroservice.trip_service.domain.entities.OutboxEvent;
import com.mircroservice.trip_service.infrastructure.documents.OutboxDocument;

@Component 
public class OutboxDocumentMapper {
    public OutboxEvent toDomain(OutboxDocument document){
        return new OutboxEvent(
            document.getId(), 
            document.getEventType(), 
            document.getAggregateId(), 
            document.getPayload(), 
            document.getPublishedAt(), 
            document.getStatus()
        );
    }
}
