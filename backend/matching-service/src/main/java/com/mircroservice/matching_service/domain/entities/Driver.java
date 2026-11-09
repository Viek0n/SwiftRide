package com.mircroservice.matching_service.domain.entities;

import java.time.Instant;

import com.mircroservice.matching_service.domain.enumerators.AccountStatus;
import com.mircroservice.matching_service.domain.enumerators.ActiveStatus;
import com.mircroservice.matching_service.domain.value_objects.Position;

import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter 
public class Driver extends User{
    private ActiveStatus activeStatus;
    private Position activePosition;

    public Driver(
        String id,
        String name,
        String phone,
        String password,
        AccountStatus accountStatus,
        Instant createdAt,
        Instant updatedAt,
        ActiveStatus activeStatus,
        Position activePosition
    ){
        super(id, name, phone, password, accountStatus, createdAt, updatedAt);
        this.activeStatus = activeStatus;
        this.activePosition = activePosition;
    }
}
