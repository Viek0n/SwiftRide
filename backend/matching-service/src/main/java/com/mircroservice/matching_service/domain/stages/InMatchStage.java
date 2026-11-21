package com.mircroservice.matching_service.domain.stages;

import com.mircroservice.matching_service.domain.entities.ActiveDriver;
import com.mircroservice.matching_service.domain.enumerators.ActiveStage;
import com.mircroservice.matching_service.domain.exceptions.stages.InvalidDriverTransitionException;

public class InMatchStage implements ActiveDriverStage{

    @Override 
    public ActiveStage getStage(){
        return ActiveStage.IN_MATCH;
    }

    @Override 
    public void goOnline(ActiveDriver driver){
        throw new InvalidDriverTransitionException( 
            ActiveStage.IN_MATCH.toString(),
            "GO_ONLINE"
        );
    }

    public void startMatching(ActiveDriver driver){
        throw new InvalidDriverTransitionException( 
            ActiveStage.IN_MATCH.toString(),
            "START_MATCHING"
        );
    }

    @Override 
    public void matchFound(ActiveDriver driver){
        throw new InvalidDriverTransitionException( 
            ActiveStage.IN_MATCH.toString(),
            "MATCH_FOUND"
        );
    }

    @Override 
    public void acceptMatch(ActiveDriver driver){
        throw new InvalidDriverTransitionException( 
            ActiveStage.IN_MATCH.toString(),
            "ACCEPT_MATCH"
        );
    }

    @Override 
    public void rejectMatch(ActiveDriver driver){
        throw new InvalidDriverTransitionException( 
            ActiveStage.IN_MATCH.toString(),
            "REJECT_MATCH"
        );
    }

    @Override 
    public void completeTrip(ActiveDriver driver)
    {
        driver.setStage(new OnlineStage());
    }
}
