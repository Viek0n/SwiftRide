package com.mircroservice.matching_service.infrastructure.persistence.mongdb.mappers;

import org.springframework.stereotype.Component;

import com.mircroservice.matching_service.domain.entities.Rider;
import com.mircroservice.matching_service.infrastructure.persistence.mongdb.documents.RiderDocument;

@Component
public class RiderDocumentMapper {

    public RiderDocument toDocument(Rider rider) {
        RiderDocument document = new RiderDocument();

        document.setId(rider.getId());
        document.setName(rider.getName());
        document.setPhone(rider.getPhone());
        document.setPassword(rider.getPassword());
        document.setAccountStatus(rider.getAccountStatus());
        document.setCreatedAt(rider.getCreatedAt());
        document.setUpdatedAt(rider.getUpdatedAt());

        return document;
    }

    public Rider toDomain(RiderDocument document) {
        return new Rider(
                document.getId(),
                document.getName(),
                document.getPhone(),
                document.getPassword(),
                document.getAccountStatus(),
                document.getCreatedAt(),
                document.getUpdatedAt()
        );
    }
}