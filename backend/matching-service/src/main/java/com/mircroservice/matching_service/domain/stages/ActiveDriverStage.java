package com.mircroservice.matching_service.domain.stages;

import com.mircroservice.matching_service.domain.entities.ActiveDriver;
import com.mircroservice.matching_service.domain.enumerators.ActiveStage;

public interface ActiveDriverStage {
    ActiveStage getStage();
    void goOnline(ActiveDriver driver);
    void startMatching(ActiveDriver driver);
    void matchFound(ActiveDriver driver);
    void acceptMatch(ActiveDriver driver);
    void rejectMatch(ActiveDriver driver);
    void completeTrip(ActiveDriver driver);
}
