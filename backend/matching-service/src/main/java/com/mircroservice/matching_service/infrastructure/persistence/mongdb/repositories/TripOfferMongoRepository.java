package com.mircroservice.matching_service.infrastructure.persistence.mongdb.repositories;

import java.util.Optional;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.mircroservice.matching_service.domain.entities.TripOffer;
import com.mircroservice.matching_service.domain.repositories.TripOfferRepository;
import com.mircroservice.matching_service.infrastructure.persistence.mongdb.mappers.TripOfferDocumentMapper;
import com.mircroservice.matching_service.infrastructure.persistence.mongdb.spring_data.SpringDataTripOfferRepository;

@Repository
public class TripOfferMongoRepository implements TripOfferRepository {
    private final SpringDataTripOfferRepository mongoRepository;
    private final TripOfferDocumentMapper mapper;

    public TripOfferMongoRepository(
        SpringDataTripOfferRepository mongoRepository,
        TripOfferDocumentMapper mapper
    ) {
        this.mongoRepository = mongoRepository;
        this.mapper = mapper;
    }

    @Override
    public TripOffer save(TripOffer attempt) {
        return mapper.toDomain(mongoRepository.save(mapper.toDocument(attempt)));
    }



    @Override
    public Optional<TripOffer> findById(String tripId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findById'");
    }

    @Override
    public List<TripOffer> getAllByTripId(String tripId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getAllByTripId'");
    }

    @Override
    public List<TripOffer> getAllBydDriverId(String tripId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getAllBydDriverId'");
    }

    @Override
    public boolean markAcceptedIfPending(String offerId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'markAcceptedIfPending'");
    }

    @Override
    public boolean markRejectedIfPending(String offerId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'markRejectedIfPending'");
    }

    @Override
    public boolean markExpiredIfPending(String offerId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'markExpiredIfPending'");
    }

    @Override
    public Optional<TripOffer> findByTripId(String tripId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findByTripId'");
    }
}
