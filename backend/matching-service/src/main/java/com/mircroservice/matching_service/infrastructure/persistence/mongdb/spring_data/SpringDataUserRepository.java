package com.mircroservice.matching_service.infrastructure.persistence.mongdb.spring_data;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

import com.mircroservice.matching_service.infrastructure.persistence.mongdb.documents.UserDocument;

public interface SpringDataUserRepository extends MongoRepository<UserDocument, String>{
    boolean existsByPhone(String phone);
    Optional<UserDocument> findByPhone(String phone);
}
