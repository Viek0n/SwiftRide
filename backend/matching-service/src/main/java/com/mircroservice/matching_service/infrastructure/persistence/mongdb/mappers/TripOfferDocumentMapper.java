package com.mircroservice.matching_service.infrastructure.persistence.mongdb.mappers;

import org.springframework.stereotype.Component;

import com.mircroservice.matching_service.domain.entities.TripOffer;
import com.mircroservice.matching_service.infrastructure.persistence.mongdb.documents.TripOfferDocument;

@Component
public class TripOfferDocumentMapper {
    public TripOfferDocument toDocument(TripOffer offer) {
        TripOfferDocument document = new TripOfferDocument();
        document.setTripId(offer.getTripId());
        document.setDriverId(offer.getDriverId());
        document.setStatus(offer.getStatus());
        document.setCreatedAt(offer.getCreatedAt());
        document.setExpiresAt(offer.getExpiresAt());
        return document;
    }

    public TripOffer toDomain(TripOfferDocument document) {
        return new TripOffer(
            document.getId(),
            document.getTripId(),
            document.getDriverId(),
            document.getStatus(),
            document.getCreatedAt(),
            document.getExpiresAt()
        );
    }
}
