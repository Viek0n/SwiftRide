package com.mircroservice.matching_service.infrastructure.persistence.mongdb.mappers;

import org.springframework.stereotype.Component;

import com.mircroservice.matching_service.domain.entities.User;
import com.mircroservice.matching_service.infrastructure.persistence.mongdb.documents.UserDocument;

@Component
public class UserDocumentMapper {
    public UserDocument toDocument(User user) {
        UserDocument document = new UserDocument();

        document.setId(user.getId());
        document.setName(user.getName());
        document.setPhone(user.getPhone());
        document.setPassword(user.getPassword());

        document.setAccountStatus(user.getAccountStatus());
        document.setCreatedAt(user.getCreatedAt());
        document.setUpdatedAt(user.getUpdatedAt());
        return document;
    }

    public User toDomain(UserDocument document) {
        return new User(
            document.getId(), 
            document.getName(), 
            document.getPhone(), 
            document.getPassword(), 
            document.getAccountStatus(), 
            document.getCreatedAt(), 
            document.getUpdatedAt());
    }
}
