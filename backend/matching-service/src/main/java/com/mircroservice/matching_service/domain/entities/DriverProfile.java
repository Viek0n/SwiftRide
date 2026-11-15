package com.mircroservice.matching_service.domain.entities;
import java.time.Instant;

import com.mircroservice.matching_service.domain.enumerators.AccountStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter 
@Setter 
@NoArgsConstructor 
@AllArgsConstructor 
public class DriverProfile {
    private String userId;

    private String  driverLicense;
    private String  licensePlate;

    private AccountStatus accountStatus;
    private Instant  createdAt;
    private Instant  updatedAt;


    public void updateProfile(
        String driverLicense,
        String licensePlate
    ){
        this.driverLicense = driverLicense;
        this.licensePlate = licensePlate;
        this.updatedAt = Instant.now();
    }

    public void suspense() {
        this.accountStatus = AccountStatus.SUSPENSED;
        this.updatedAt = Instant.now();
    }

    public void active(){
        this.accountStatus = AccountStatus.ACTIVE;
        this.updatedAt = Instant.now();
    }
}
