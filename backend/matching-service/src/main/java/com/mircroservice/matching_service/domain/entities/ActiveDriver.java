package com.mircroservice.matching_service.domain.entities;

import com.mircroservice.matching_service.domain.enumerators.ActiveStage;
import com.mircroservice.matching_service.domain.stages.ActiveDriverStage;
import com.mircroservice.matching_service.domain.stages.OnlineStage;
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

    private Position activePosition;
    private ActiveDriverStage stage;

    public ActiveDriver(String userId, Position activePosition){
        this.userId = userId;
        this.activePosition = activePosition;
        this.stage = new OnlineStage();
    }
    
    public ActiveStage getActiveStage(){
        return this.stage.getStage();
    }

    public void setStage(ActiveDriverStage stage){
        this.stage = stage;
    }

    public void goOnline(){
        stage.goOnline(this);
    }

    public void startMatching(){
        stage.startMatching(this);
    }

    public void matchFound(){
        stage.matchFound(this);
    }

    public void acceptMatch(){
        stage.acceptMatch(this);
    }

    public void rejectMatch(){
        stage.rejectMatch(this);
    }

    public void completeTrip(){
        stage.completeTrip(this);
    }
}
