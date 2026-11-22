package com.mircroservice.matching_service.infrastructure.persistence.mongdb.repositories;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.mircroservice.matching_service.domain.entities.DriverProfile;
import com.mircroservice.matching_service.domain.enumerators.AccountStatus;
import com.mircroservice.matching_service.domain.repositories.DriverProfileRepository;
import com.mircroservice.matching_service.infrastructure.persistence.mongdb.documents.DriverProfileDocument;
import com.mircroservice.matching_service.infrastructure.persistence.mongdb.mappers.DriverDocumentMapper;
import com.mircroservice.matching_service.infrastructure.persistence.mongdb.spring_data.SpringDataDriverProfileRepository;

@Repository
public class DriverProfileMongoRepository implements DriverProfileRepository {
    private final SpringDataDriverProfileRepository mongoRepository;
    private final DriverDocumentMapper mapper;

    public DriverProfileMongoRepository(
        SpringDataDriverProfileRepository mongoRepository,
        DriverDocumentMapper mapper
    ) {
        this.mongoRepository = mongoRepository;
        this.mapper = mapper;
    }

    @Override
    public DriverProfile save(DriverProfile driver) {
        DriverProfileDocument saved = mongoRepository.save(mapper.toDocument(driver));
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<DriverProfile> findByUserId(String userId) {
        return mongoRepository.findByUserId(userId).map(mapper::toDomain);
    }

    @Override
    public List<DriverProfile> findAll() {
        return mongoRepository.findAll().stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public boolean existsByUserId(String id) {
        return mongoRepository.existsByUserId(id);
    }

    @Override
    public void softDeleteById(String id) {
        DriverProfile driver = findByUserId(id).orElseThrow(
            () -> new IllegalArgumentException("Không tìm thấy tài xế"));
        driver.setAccountStatus(AccountStatus.SOFT_DELETE);
        driver.setUpdatedAt(Instant.now());
        save(driver);
    }

    @Override
    public void deleteById(String id) {
        mongoRepository.deleteById(id);
    }
}
