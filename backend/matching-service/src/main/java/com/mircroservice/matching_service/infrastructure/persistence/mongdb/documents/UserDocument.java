package com.mircroservice.matching_service.infrastructure.persistence.mongdb.documents;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.mircroservice.matching_service.domain.enumerators.AccountStatus;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter 
@Setter
@NoArgsConstructor
@Document(collection = "users")
public class UserDocument {

    @Id
    private String id;
    private String name;
    private String phone;
    private String password;

    private AccountStatus accountStatus;
    private Instant createdAt;
    private Instant updatedAt;
}
