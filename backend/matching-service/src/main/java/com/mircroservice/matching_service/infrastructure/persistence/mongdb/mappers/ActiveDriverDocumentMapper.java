package com.mircroservice.matching_service.infrastructure.persistence.mongdb.mappers;

import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.stereotype.Component;

import com.mircroservice.matching_service.domain.entities.ActiveDriver;
import com.mircroservice.matching_service.domain.enumerators.ActiveStage;
import com.mircroservice.matching_service.domain.stages.ActiveDriverStage;
import com.mircroservice.matching_service.domain.stages.InMatchStage;
import com.mircroservice.matching_service.domain.stages.MatchFoundStage;
import com.mircroservice.matching_service.domain.stages.MatchingStage;
import com.mircroservice.matching_service.domain.stages.OnlineStage;
import com.mircroservice.matching_service.domain.value_objects.Position;
import com.mircroservice.matching_service.infrastructure.persistence.mongdb.documents.ActiveDriverDocument;

@Component
public class ActiveDriverDocumentMapper {
    public ActiveDriverDocument toDocument(ActiveDriver activeDriver) {
        ActiveDriverDocument document = new ActiveDriverDocument();
        document.setUserId(activeDriver.getUserId());
        document.setActiveStage(activeDriver.getActiveStage());
        Position position = activeDriver.getActivePosition();
        document.setActivePosition(new GeoJsonPoint(position.longitude(), position.latitude()));
        return document;
    }

    public ActiveDriver toDomain(ActiveDriverDocument document) {
        GeoJsonPoint point = document.getActivePosition();
        Position position = new Position(point.getY(), point.getX());
        return new ActiveDriver(document.getUserId(),position, toDomainStage(document.getActiveStage()));
    }

    public ActiveDriverStage toDomainStage(ActiveStage stage){
        return switch (stage) {
            case ONLINE -> new OnlineStage();
            case MATCHING -> new MatchingStage();
            case MATCH_FOUND -> new MatchFoundStage();
            case IN_MATCH -> new InMatchStage();
        };
    }
}
