package com.mircroservice.payment_service.domain.repositories;

import java.util.Optional;
import java.util.List;

import com.mircroservice.payment_service.domain.entities.Bill;

public interface BillRepository {
    Optional<Bill> findByTripId(String tripId);

    Optional<Bill> findByAttempts_Id(String attemptId);

    List<Bill> findTop50ByOutboxEvents_PublishedAtIsNull();

    Bill save(Bill bill);
}
