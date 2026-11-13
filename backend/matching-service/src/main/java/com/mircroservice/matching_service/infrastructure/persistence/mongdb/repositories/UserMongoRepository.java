package com.mircroservice.matching_service.infrastructure.persistence.mongdb.repositories;

import java.util.Optional;

import com.mircroservice.matching_service.domain.entities.User;
import com.mircroservice.matching_service.domain.enumerators.AccountStatus;
import com.mircroservice.matching_service.domain.repositories.UserRepository;
import com.mircroservice.matching_service.infrastructure.persistence.mongdb.documents.UserDocument;
import com.mircroservice.matching_service.infrastructure.persistence.mongdb.mappers.UserDocumentMapper;
import com.mircroservice.matching_service.infrastructure.persistence.mongdb.spring_data.SpringDataUserRepository;

public class UserMongoRepository implements UserRepository{
    private final SpringDataUserRepository mongoRepository;
    private final UserDocumentMapper mapper;
    
    public UserMongoRepository(
        SpringDataUserRepository mongoRepository,
        UserDocumentMapper mapper
    ) {
        this.mongoRepository = mongoRepository;
        this.mapper = mapper;
    }

    @Override 
    public User save(User user){
        UserDocument document = mapper.toDocument(user);
        UserDocument saved = mongoRepository.save(document);

        return mapper.toDomain(saved);
    }

    @Override
    public Boolean existByPhone(String phone){
        return mongoRepository.existByPhone(phone);
    }

    @Override 
    public Optional<User> findById(String id){
        return mongoRepository.findById(id).map(mapper::toDomain);
    }

    @Override 
    public void softDeleteById(String id){
        Optional<User> optionalUser = findById(id);
        if (optionalUser.isEmpty()) {
            throw new RuntimeException("User not found: " + id);
        }

        User user = optionalUser.get();
        user.setAccountStatus(AccountStatus.SOFT_DELETE);

        save(user);
        return;
    }

    @Override 
    public void deleteById(String id){
        mongoRepository.deleteById(id);
        return;
    }
}


