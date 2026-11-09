package com.mircroservice.matching_service.infrastructure.persistence.mongdb.documents;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.mircroservice.matching_service.domain.enumerators.AccountStatus;
import com.mircroservice.matching_service.domain.enumerators.ActiveStatus;
import com.mircroservice.matching_service.domain.value_objects.Position;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter 
@Setter 
@NoArgsConstructor 
@Document(collection = "drivers")
public class DriverDocument {
    @Id 
    String id;

    String name;
    String phone;
    String password;

    AccountStatus accountStatus;
    Instant createdAt;
    Instant updatedAt;

    ActiveStatus activeStatus;
    Position activePosition;
}
