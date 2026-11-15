package com.mircroservice.matching_service.domain.entities;

import com.mircroservice.matching_service.domain.enumerators.ActiveStatus;
import com.mircroservice.matching_service.domain.value_objects.Position;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter 
@Setter 
@NoArgsConstructor 
@AllArgsConstructor 
public class ActiveDriver {
    private String userId;

    private ActiveStatus activeStatus;
    private Position activePosition;
}
