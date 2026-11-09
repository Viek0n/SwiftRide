package com.mircroservice.matching_service.domain.entities;

import java.time.Instant;

import com.mircroservice.matching_service.domain.enumerators.AccountStatus;

public class Rider extends User{
    public Rider(
        String id,
        String name,
        String phone,
        String password,
        AccountStatus accountStatus,
        Instant createdAt,
        Instant updatedAt
    ){
        super(id, name, phone, password, accountStatus, createdAt, updatedAt);
    }
}
