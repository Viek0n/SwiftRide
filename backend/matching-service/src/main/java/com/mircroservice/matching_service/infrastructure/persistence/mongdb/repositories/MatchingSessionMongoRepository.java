package com.mircroservice.matching_service.infrastructure.persistence.mongdb.repositories;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import com.mircroservice.matching_service.domain.entities.MatchingSession;
import com.mircroservice.matching_service.domain.enumerators.MatchingStatus;
import com.mircroservice.matching_service.domain.repositories.MatchingSessionRepository;
import com.mircroservice.matching_service.infrastructure.persistence.mongdb.mappers.MatchingSessionMapper;
import com.mircroservice.matching_service.infrastructure.persistence.mongdb.spring_data.SpringDataMatchingSessionMongoRepository;
import com.mircroservice.matching_service.infrastructure.persistence.mongdb.documents.MatchingSessionDocument;

@Repository 
public class MatchingSessionMongoRepository implements MatchingSessionRepository{
    private final SpringDataMatchingSessionMongoRepository mongoRepository;
    private final MatchingSessionMapper mapper;
    private final MongoTemplate mongoTemplate;

    public MatchingSessionMongoRepository(
            SpringDataMatchingSessionMongoRepository mongoRepository,
            MatchingSessionMapper mapper,
            MongoTemplate mongoTemplate) {
        this.mongoRepository = mongoRepository;
        this.mapper = mapper;
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public MatchingSession save(MatchingSession session) {
        MatchingSessionDocument saved = mongoRepository.save(mapper.toDocument(session));
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<MatchingSession> findById(String id) {
        return mongoRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<MatchingSession> findByTripId(String tripId) {
        return mongoRepository.findByTripId(tripId).map(mapper::toDomain);
    }

    @Override
    public Optional<MatchingSession> findByCurrentOfferId(String offerId) {
        return mongoRepository.findByCurrentOfferId(offerId).map(mapper::toDomain);
    }

    @Override
    public boolean startWaitingIfSearching(String sessionId, String driverId, String offerId, Instant expiresAt) {
        Query query = new Query(Criteria.where("id").is(sessionId).and("status").is(MatchingStatus.SEARCHING));
        Update update = new Update()
                .set("status", MatchingStatus.WAITING_FOR_DRIVER)
                .set("currentDriverId", driverId)
                .set("currentOfferId", offerId)
                .set("expiresAt", expiresAt)
                .inc("attempt", 1);
        return mongoTemplate.updateFirst(query, update, MatchingSessionDocument.class).getModifiedCount() > 0;
    }

    @Override
    public boolean markMatchedIfWaiting(String offerId) {
        Query query = new Query(Criteria.where("currentOfferId").is(offerId).and("status").is(MatchingStatus.WAITING_FOR_DRIVER));
        Update update = new Update().set("status", MatchingStatus.MATCHED);
        return mongoTemplate.updateFirst(query, update, MatchingSessionDocument.class).getModifiedCount() > 0;
    }

    @Override
    public boolean markSearchingIfWaiting(String offerId) {
        Query query = new Query(Criteria.where("currentOfferId").is(offerId).and("status").is(MatchingStatus.WAITING_FOR_DRIVER));
        Update update = new Update()
                .set("status", MatchingStatus.SEARCHING)
                .unset("currentDriverId")
                .unset("currentOfferId")
                .unset("expiresAt");
        return mongoTemplate.updateFirst(query, update, MatchingSessionDocument.class).getModifiedCount() > 0;
    }

    @Override
    public List<MatchingSession> findExpiredSessions(Instant now) {
        Query query = new Query(Criteria.where("expiresAt").lte(now)
                .and("status").is(MatchingStatus.WAITING_FOR_DRIVER));
        return mongoTemplate.find(query, MatchingSessionDocument.class).stream()
                .map(mapper::toDomain)
                .toList();
    }
    
}
