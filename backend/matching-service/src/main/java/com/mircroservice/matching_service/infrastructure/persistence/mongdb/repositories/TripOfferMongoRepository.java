package com.mircroservice.matching_service.infrastructure.persistence.mongdb.repositories;

import java.util.Optional;
import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import com.mircroservice.matching_service.domain.entities.TripOffer;
import com.mircroservice.matching_service.domain.enumerators.OfferStatus;
import com.mircroservice.matching_service.domain.repositories.TripOfferRepository;
import com.mircroservice.matching_service.infrastructure.persistence.mongdb.mappers.TripOfferDocumentMapper;
import com.mircroservice.matching_service.infrastructure.persistence.mongdb.spring_data.SpringDataTripOfferRepository;
import com.mircroservice.matching_service.infrastructure.persistence.mongdb.documents.TripOfferDocument;

@Repository
public class TripOfferMongoRepository implements TripOfferRepository {
    private final SpringDataTripOfferRepository mongoRepository;
    private final TripOfferDocumentMapper mapper;
    private final MongoTemplate mongoTemplate;

    public TripOfferMongoRepository(
        SpringDataTripOfferRepository mongoRepository,
        TripOfferDocumentMapper mapper,
        MongoTemplate mongoTemplate
    ) {
        this.mongoRepository = mongoRepository;
        this.mapper = mapper;
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public TripOffer save(TripOffer attempt) {
        return mapper.toDomain(mongoRepository.save(mapper.toDocument(attempt)));
    }



    @Override
    public Optional<TripOffer> findById(String tripId) {
        return mongoRepository.findById(tripId).map(mapper::toDomain);
    }

    @Override
    public List<TripOffer> getAllByTripId(String tripId) {
        return mongoRepository.findByTripId(tripId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<TripOffer> getAllBydDriverId(String tripId) {
        return mongoRepository.findAll().stream()
                .filter(document -> tripId.equals(document.getDriverId()))
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public boolean markAcceptedIfPending(String offerId) {
        return markStatusIfPending(offerId, OfferStatus.ACCEPTED);
    }

    @Override
    public boolean markRejectedIfPending(String offerId) {
        return markStatusIfPending(offerId, OfferStatus.REJECTED);
    }

    @Override
    public boolean markExpiredIfPending(String offerId) {
        return markStatusIfPending(offerId, OfferStatus.EXPIRED);
    }

    @Override
    public Optional<TripOffer> findByTripId(String tripId) {
        return mongoRepository.findByTripId(tripId).stream().findFirst().map(mapper::toDomain);
    }

    private boolean markStatusIfPending(String offerId, OfferStatus status) {
        Query query = new Query(Criteria.where("id").is(offerId).and("status").is(OfferStatus.PENDING));
        Update update = new Update().set("status", status);
        return mongoTemplate.updateFirst(query, update, TripOfferDocument.class).getModifiedCount() > 0;
    }
}
