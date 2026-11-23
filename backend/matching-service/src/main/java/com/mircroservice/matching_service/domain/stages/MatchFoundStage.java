package com.mircroservice.matching_service.domain.stages;

import com.mircroservice.matching_service.domain.entities.ActiveDriver;
import com.mircroservice.matching_service.domain.enumerators.ActiveStage;
import com.mircroservice.matching_service.domain.exceptions.stages.InvalidDriverTransitionException;

public class MatchFoundStage implements ActiveDriverStage{
    @Override 
    public ActiveStage getStage(){
        return ActiveStage.MATCH_FOUND;
    }

    @Override 
    public void goOnline(ActiveDriver driver){
        throw new InvalidDriverTransitionException( 
            ActiveStage.MATCH_FOUND.toString(),
            "GO_ONLINE"
        );
    }

    public void startMatching(ActiveDriver driver){
        throw new InvalidDriverTransitionException( 
            ActiveStage.MATCH_FOUND.toString(),
            "START_MATCHING"
        );
    }

    @Override 
    public void matchFound(ActiveDriver driver){

    }

    @Override 
    public void acceptMatch(ActiveDriver driver){
        driver.setStage(new InMatchStage());
    }

    @Override 
    public void rejectMatch(ActiveDriver driver){
        driver.setStage(new OnlineStage());
    }

    @Override 
    public void completeTrip(ActiveDriver driver)
    {
        throw new InvalidDriverTransitionException(
            ActiveStage.MATCH_FOUND.toString(),
            "COMPLETE_TRIP"
        );
    }
}
