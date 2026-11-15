package com.mircroservice.matching_service.infrastructure.persistence.mongdb.repositories;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.stereotype.Repository;

import com.mircroservice.matching_service.domain.entities.ActiveDriver;
import com.mircroservice.matching_service.domain.enumerators.ActiveStatus;
import com.mircroservice.matching_service.domain.repositories.ActiveDriverRepository;
import com.mircroservice.matching_service.domain.value_objects.Position;
import com.mircroservice.matching_service.infrastructure.persistence.mongdb.documents.ActiveDriverDocument;
import com.mircroservice.matching_service.infrastructure.persistence.mongdb.mappers.ActiveDriverDocumentMapper;
import com.mircroservice.matching_service.infrastructure.persistence.mongdb.spring_data.SpringDataActiveDriverRepository;

@Repository
public class ActiveDriverMongoRepository implements ActiveDriverRepository {
    private final SpringDataActiveDriverRepository mongoRepository;
    private final ActiveDriverDocumentMapper mapper;

    public ActiveDriverMongoRepository(
        SpringDataActiveDriverRepository mongoRepository,
        ActiveDriverDocumentMapper mapper
    ) {
        this.mongoRepository = mongoRepository;
        this.mapper = mapper;
    }

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
    public List<ActiveDriver> findByActiveStatus(ActiveStatus activeStatus) {
        return mongoRepository.findByActiveStatus(activeStatus).stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public boolean transitionStatus(
        String userId,
        ActiveStatus currentStatus,
        ActiveStatus nextStatus
    ) {
        return mongoRepository.updateStatusIfCurrent(userId, currentStatus, nextStatus) > 0;
    }

    @Override
    public boolean updatePositionIfStatus(
        String userId,
        ActiveStatus currentStatus,
        Position position
    ) {
        return mongoRepository.updatePositionIfCurrent(
            userId,
            currentStatus,
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
}
