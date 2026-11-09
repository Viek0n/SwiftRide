package com.mircroservice.matching_service.infrastructure.persistence.mongdb.mappers;


import com.mircroservice.matching_service.domain.entities.Driver;
import com.mircroservice.matching_service.infrastructure.persistence.mongdb.documents.DriverDocument;

public class DriverDocumentMapper {
    public DriverDocument toDocument(Driver driver){
        DriverDocument document = new DriverDocument();

        document.setId(driver.getId());
        document.setName(driver.getName());
        document.setPhone(driver.getPhone());
        document.setPassword(driver.getPassword());

        document.setAccountStatus(driver.getAccountStatus());
        document.setCreatedAt(driver.getCreatedAt());
        document.setUpdatedAt(driver.getUpdatedAt());

        document.setActiveStatus(driver.getActiveStatus());
        document.setActivePosition(driver.getActivePosition());
        return document;
    }

    public Driver toDomain(DriverDocument document){
        return new Driver(
            document.getId(), 
            document.getName(), 
            document.getPhone(), 
            document.getPassword(), 
            document.getAccountStatus(), 
            document.getCreatedAt(), 
            document.getUpdatedAt(), 
            document.getActiveStatus(), 
            document.getActivePosition());
    }
}
