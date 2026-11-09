package com.mircroservice.matching_service.infrastructure.persistence.mongdb.repositories;

import java.util.Optional;

import com.mircroservice.matching_service.domain.entities.Driver;
import com.mircroservice.matching_service.domain.repositories.DriverRepository;
import com.mircroservice.matching_service.infrastructure.persistence.mongdb.documents.DriverDocument;
import com.mircroservice.matching_service.infrastructure.persistence.mongdb.mappers.DriverDocumentMapper;
import com.mircroservice.matching_service.infrastructure.persistence.mongdb.spring_data.SpringDataDriverRepository;

public class DriverMongoRepository implements DriverRepository{
    private final SpringDataDriverRepository mongoRepository;
    private final DriverDocumentMapper mapper;

    public DriverMongoRepository(
        SpringDataDriverRepository mongoRepository,
        DriverDocumentMapper mapper
    ){
        this.mongoRepository = mongoRepository;
        this.mapper = mapper;
    }

    @Override 
    public Driver save(Driver driver){
        DriverDocument document = mapper.toDocument(driver);
        
        DriverDocument saved = mongoRepository.save(document);
        return mapper.toDomain(saved);
    }

    @Override 
    public Optional<Driver> findById(String id){
        return mongoRepository.findById(id).map(mapper::toDomain);
    }

    @Override 
    public void deleteById(String id){
        mongoRepository.deleteById(id);
        return;
    }
}
