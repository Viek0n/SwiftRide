package com.mircroservice.matching_service.infrastructure.persistence.mongdb.mappers;

import org.springframework.stereotype.Component;

import com.mircroservice.matching_service.domain.entities.DriverProfile;
import com.mircroservice.matching_service.infrastructure.persistence.mongdb.documents.DriverProfileDocument;

@Component
public class DriverDocumentMapper {
    public DriverProfileDocument toDocument(DriverProfile driver){
        DriverProfileDocument document = new DriverProfileDocument();

        document.setUserId(driver.getUserId());
        document.setDriverLicense(driver.getDriverLicense());
        document.setLicensePlate(driver.getLicensePlate());

        document.setAccountStatus(driver.getAccountStatus());
        document.setCreatedAt(driver.getCreatedAt());
        document.setUpdatedAt(driver.getUpdatedAt());
        return document;
    }

    public DriverProfile toDomain(DriverProfileDocument document){
        return new DriverProfile(
            document.getUserId(), 
            document.getDriverLicense(), 
            document.getLicensePlate(), 
            document.getAccountStatus(), 
            document.getCreatedAt(), 
            document.getUpdatedAt());
    }
}
