package com.mircroservice.payment_service.infrastructure.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.mircroservice.payment_service.domain.entities.Bill;
import com.mircroservice.payment_service.domain.repositories.BillRepository;

public interface BillMongoRepository extends MongoRepository<Bill, String>, BillRepository {
}
