package com.mircroservice.matching_service.infrastructure.persistence.mongdb.repositories;

import java.util.Optional;

import com.mircroservice.matching_service.domain.entities.Rider;
import com.mircroservice.matching_service.domain.repositories.RiderRepository;
import com.mircroservice.matching_service.infrastructure.persistence.mongdb.documents.RiderDocument;
import com.mircroservice.matching_service.infrastructure.persistence.mongdb.mappers.RiderDocumentMapper;
import com.mircroservice.matching_service.infrastructure.persistence.mongdb.spring_data.SpringDataRiderRepository;

public class RiderMongoRepository implements RiderRepository{
    private final SpringDataRiderRepository mongoRepository;
    private final RiderDocumentMapper mapper;
    
    public RiderMongoRepository(
        SpringDataRiderRepository mongoRepository,
        RiderDocumentMapper mapper
    ) {
        this.mongoRepository = mongoRepository;
        this.mapper = mapper;
    }
    @Override 
    public Rider save(Rider rider){
        RiderDocument document = mapper.toDocument(rider);
        RiderDocument saved = mongoRepository.save(document);

        return mapper.toDomain(saved);
    }

    public Optional<Rider> findById(String id){
        return mongoRepository.findById(id).map(mapper::toDomain);
    }

    public void deleteById(String id){
        mongoRepository.deleteById(id);
        return;
    }
}


