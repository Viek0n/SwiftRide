package com.mircroservice.matching_service.infrastructure.persistence.mongdb.mappers;

import com.mircroservice.matching_service.domain.entities.User;
import com.mircroservice.matching_service.infrastructure.persistence.mongdb.documents.UserDocument;

public class UserDocumentMapper {
    public UserDocument toDocument(User driver){
        UserDocument document = new UserDocument();

        document.setId(driver.getId());
        document.setName(driver.getName());
        document.setPhone(driver.getPhone());
        document.setPassword(driver.getPassword());

        document.setAccountStatus(driver.getAccountStatus());
        document.setCreatedAt(driver.getCreatedAt());
        document.setUpdatedAt(driver.getUpdatedAt());
        return document;
    }

    public User toDomain(UserDocument document){
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
