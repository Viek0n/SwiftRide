package com.mircroservice.matching_service.infrastructure.persistence.mongdb.spring_data;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.mircroservice.matching_service.infrastructure.persistence.mongdb.documents.UserDocument;

public interface SpringDataUserRepository extends MongoRepository<UserDocument, String>{
    Boolean existByPhone(String phone);
    void softDeleteById(String id);
}
