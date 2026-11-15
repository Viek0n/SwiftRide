package com.mircroservice.matching_service.infrastructure.persistence.mongdb.mappers;

import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.stereotype.Component;

import com.mircroservice.matching_service.domain.entities.ActiveDriver;
import com.mircroservice.matching_service.domain.value_objects.Position;
import com.mircroservice.matching_service.infrastructure.persistence.mongdb.documents.ActiveDriverDocument;

@Component
public class ActiveDriverDocumentMapper {
    public ActiveDriverDocument toDocument(ActiveDriver activeDriver) {
        ActiveDriverDocument document = new ActiveDriverDocument();
        document.setUserId(activeDriver.getUserId());
        document.setActiveStatus(activeDriver.getActiveStatus());
        Position position = activeDriver.getActivePosition();
        document.setActivePosition(new GeoJsonPoint(position.longitude(), position.latitude()));
        return document;
    }

    public ActiveDriver toDomain(ActiveDriverDocument document) {
        GeoJsonPoint point = document.getActivePosition();
        Position position = new Position(point.getY(), point.getX());
        return new ActiveDriver(document.getUserId(), document.getActiveStatus(), position);
    }
}
