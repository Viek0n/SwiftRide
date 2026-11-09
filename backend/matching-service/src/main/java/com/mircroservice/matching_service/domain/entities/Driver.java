package com.mircroservice.matching_service.domain.entities;

import com.mircroservice.matching_service.domain.enumerators.DriverStatus;
import com.mircroservice.matching_service.domain.value_objects.Position;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter 
@Setter 
@NoArgsConstructor 
@AllArgsConstructor 
@SuperBuilder 
public class Driver extends User{
    private DriverStatus activeStatus;
    private Position activePosition;
}
