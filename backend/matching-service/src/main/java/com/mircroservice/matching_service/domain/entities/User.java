package com.mircroservice.matching_service.domain.entities;

import java.time.Instant;

import com.mircroservice.matching_service.domain.enumerators.AccountStatus;
import com.mircroservice.matching_service.domain.value_objects.Position;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter 
@Setter 
@AllArgsConstructor 
@NoArgsConstructor 
@SuperBuilder
public class User {
    private String id;
    private String name;
    private String phone;
    private String password;

    private AccountStatus accountStatus;
    private Instant createdAt;
    private Instant updatedAt;

    public void updateProfile(
        String name,
        String phone
    ){
        this.name = name;
        this.phone = phone;
        this.updatedAt = Instant.now();
    }

    public void deactivate() {
        this.accountStatus = AccountStatus.INACTIVE;
        this.updatedAt = Instant.now();
    }

    public void active(){
        this.accountStatus = AccountStatus.ACTIVE;
        this.updatedAt = Instant.now();
    }

    public void changePassword(String password){
        this.password = password;
        this.updatedAt = Instant.now();
    }
    
}
