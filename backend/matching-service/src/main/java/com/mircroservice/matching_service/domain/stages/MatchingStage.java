package com.mircroservice.matching_service.domain.stages;

import com.mircroservice.matching_service.domain.entities.ActiveDriver;
import com.mircroservice.matching_service.domain.enumerators.ActiveStage;
import com.mircroservice.matching_service.domain.exceptions.stages.InvalidDriverTransitionException;

public class MatchingStage implements ActiveDriverStage{

    @Override 
    public ActiveStage getStage(){
        return ActiveStage.MATCHING;
    }

    @Override 
    public void goOnline(ActiveDriver driver){
        throw new InvalidDriverTransitionException( 
            ActiveStage.MATCHING.toString(),
            "GO_ONLINE"
        );
    }

    public void startMatching(ActiveDriver driver){

    }

    @Override 
    public void matchFound(ActiveDriver driver){
        driver.setStage(new MatchFoundStage());
    }

    @Override 
    public void acceptMatch(ActiveDriver driver){
        throw new InvalidDriverTransitionException(
            ActiveStage.MATCHING.toString(),
            "ACCEPT_MATCH"
        );
    }

    @Override 
    public void rejectMatch(ActiveDriver driver){
        throw new InvalidDriverTransitionException(
            ActiveStage.MATCHING.toString(),
            "REJECT_MATCH"
        );
    }

    @Override 
    public void completeTrip(ActiveDriver driver)
    {
        throw new InvalidDriverTransitionException(
            ActiveStage.MATCHING.toString(),
            "COMPLETE_TRIP"
        );
    }
}
