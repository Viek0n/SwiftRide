package com.mircroservice.matching_service.domain.stages;

import com.mircroservice.matching_service.domain.entities.ActiveDriver;
import com.mircroservice.matching_service.domain.enumerators.ActiveStage;
import com.mircroservice.matching_service.domain.exceptions.stages.InvalidDriverTransitionException;

public class OnlineStage implements ActiveDriverStage{

    @Override 
    public ActiveStage getStage(){
        return ActiveStage.ONLINE;
    }

    @Override 
    public void goOnline(ActiveDriver driver){

    }

    public void startMatching(ActiveDriver driver){
        driver.setStage(new MatchingStage());
    }

    @Override 
    public void matchFound(ActiveDriver driver){
        throw new InvalidDriverTransitionException(
            ActiveStage.ONLINE.toString(),
            "MATCH_FOUND"
        );
    }

    @Override 
    public void acceptMatch(ActiveDriver driver){
        throw new InvalidDriverTransitionException(
            ActiveStage.ONLINE.toString(),
            "ACCEPT_MATCH"
        );
    }

    @Override 
    public void rejectMatch(ActiveDriver driver){
        throw new InvalidDriverTransitionException(
            ActiveStage.ONLINE.toString(),
            "REJECT_MATCH"
        );
    }

    @Override 
    public void completeTrip(ActiveDriver driver)
    {
        throw new InvalidDriverTransitionException(
            ActiveStage.ONLINE.toString(),
            "COMPLETE_TRIP"
        );
    }
}
