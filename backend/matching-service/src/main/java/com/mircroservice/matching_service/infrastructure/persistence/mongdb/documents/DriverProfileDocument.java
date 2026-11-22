package com.mircroservice.matching_service.infrastructure.persistence.mongdb.documents;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import com.mircroservice.matching_service.domain.enumerators.AccountStatus;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter  
@Setter 
@NoArgsConstructor 
@Document (collection = "driver_profiles")
public class DriverProfileDocument {
    @Field(targetType = FieldType.OBJECT_ID)
    private String userId;

    private String  driverLicense;
    private String  licensePlate;

    private AccountStatus accountStatus;
    private Instant createdAt;
    private Instant updatedAt;
}
