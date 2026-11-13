package com.mircroservice.matching_service.infrastructure.persistence.mongdb.mappers;

import com.mircroservice.matching_service.domain.entities.DriverProfile;
import com.mircroservice.matching_service.infrastructure.persistence.mongdb.documents.DriverProfileDocument;

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

    public DriverProfile toDomain(DriverProfile document){
        return new DriverProfile(
            document.getUserId(), 
            document.getDriverLicense(), 
            document.getLicensePlate(), 
            document.getAccountStatus(), 
            document.getCreatedAt(), 
            document.getUpdatedAt());
    }
}
