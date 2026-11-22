package com.mircroservice.matching_service.infrastructure.persistence.mongdb.repositories;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Metrics;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.NearQuery;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.mircroservice.matching_service.domain.constants.MatchingConstant;
import com.mircroservice.matching_service.domain.entities.ActiveDriver;
import com.mircroservice.matching_service.domain.enumerators.ActiveStage;
import com.mircroservice.matching_service.domain.repositories.ActiveDriverRepository;
import com.mircroservice.matching_service.domain.value_objects.Position;
import com.mircroservice.matching_service.infrastructure.persistence.mongdb.documents.ActiveDriverDocument;
import com.mircroservice.matching_service.infrastructure.persistence.mongdb.mappers.ActiveDriverDocumentMapper;
import com.mircroservice.matching_service.infrastructure.persistence.mongdb.spring_data.SpringDataActiveDriverRepository;

import lombok.AllArgsConstructor;

@AllArgsConstructor 
@Repository
public class ActiveDriverMongoRepository implements ActiveDriverRepository {
    private final SpringDataActiveDriverRepository mongoRepository;
    private final ActiveDriverDocumentMapper mapper;
    private final MongoTemplate mongoTemplate;

    @Override
    public ActiveDriver save(ActiveDriver activeDriver) {
        ActiveDriverDocument saved = mongoRepository.save(mapper.toDocument(activeDriver));
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<ActiveDriver> findById(String id) {
        return mongoRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<ActiveDriver> findByActiveStage(ActiveStage activeStage) {
        return mongoRepository.findByActiveStage(activeStage).stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public boolean updateStatus(
        String userId,
        ActiveStage status
    ) {
        return mongoRepository.updateStatus(userId, status) > 0;
    }

    @Override
    public boolean updatePosition(
        String userId,
        Position position
    ) {
        return mongoRepository.updatePosition(
            userId,
            new GeoJsonPoint(position.longitude(), position.latitude())
        ) > 0;
    }

    @Override
    public boolean existsById(String id) {
        return mongoRepository.existsById(id);
    }

    @Override
    public void deleteById(String id) {
        mongoRepository.deleteById(id);
    }

    @Override 
    public Optional<ActiveDriver> findNearestAvailableDriver(
        Position riderPosition, 
        ArrayList<String> excludedDriverIds
    ){
        GeoJsonPoint point = new GeoJsonPoint(
            riderPosition.longitude(),
            riderPosition.latitude()
        );

        Query query = new Query();

        query.addCriteria(
            Criteria.where("status").is("ONLINE")
        );

        if (excludedDriverIds != null && !excludedDriverIds.isEmpty()) {
            query.addCriteria(
                Criteria.where("_id").nin(excludedDriverIds)
            );
        }

        NearQuery nearQuery = NearQuery.near(point)
            .maxDistance(MatchingConstant.SEARCH_RADIUS)
            .query(query)
            .limit(1);

        GeoResults<ActiveDriverDocument> results =
            mongoTemplate.geoNear(
                nearQuery,
                ActiveDriverDocument.class
            );

        return results.getContent()
            .stream()
            .findFirst()
            .map(result -> mapper.toDomain(result.getContent()));
    }
     
    @Override 
    public Boolean tryReserveDriver(String driverId, String tripId){

        return true;
    }

    @Override
    public boolean releaseDriverIfReservedForTrip(String driverId, String tripId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'releaseDriverIfReservedForTrip'");
    }
}
