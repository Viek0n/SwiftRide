package com.mircroservice.matching_service.domain.entities;

import com.mircroservice.matching_service.domain.value_objects.Address;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter 
@Setter 
@NoArgsConstructor 
@AllArgsConstructor 
public class Driver {
    private String id;
    private String name;
    private String phone;
    private String license_id;
    private Address address;
    private String license_plate;
    private String bike;
}
