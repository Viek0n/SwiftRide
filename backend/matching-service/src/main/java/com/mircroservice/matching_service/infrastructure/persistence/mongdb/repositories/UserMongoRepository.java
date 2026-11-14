package com.mircroservice.matching_service.infrastructure.persistence.mongdb.repositories;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.mircroservice.matching_service.domain.entities.User;
import com.mircroservice.matching_service.domain.enumerators.AccountStatus;
import com.mircroservice.matching_service.domain.repositories.UserRepository;
import com.mircroservice.matching_service.infrastructure.persistence.mongdb.documents.UserDocument;
import com.mircroservice.matching_service.infrastructure.persistence.mongdb.mappers.UserDocumentMapper;
import com.mircroservice.matching_service.infrastructure.persistence.mongdb.spring_data.SpringDataUserRepository;

@Repository
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
    public boolean existsByPhone(String phone){
        return mongoRepository.existsByPhone(phone);
    }

    @Override 
    public Optional<User> findById(String id){
        return mongoRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<User> findByPhone(String phone) {
        return mongoRepository.findByPhone(phone).map(mapper::toDomain);
    }

    @Override
    public List<User> findAll() {
        return mongoRepository.findAll().stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override 
    public void softDeleteById(String id){
        Optional<User> optionalUser = findById(id);
        if (optionalUser.isEmpty()) {
            throw new RuntimeException("User not found: " + id);
        }

        User user = optionalUser.get();
        user.setAccountStatus(AccountStatus.SOFT_DELETE);
        user.setUpdatedAt(Instant.now());

        save(user);
        return;
    }

    @Override 
    public void deleteById(String id){
        mongoRepository.deleteById(id);
        return;
    }
}


