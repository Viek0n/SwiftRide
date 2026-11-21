package com.mircroservice.matching_service.domain.exceptions.stages;

public class InvalidDriverTransitionException
        extends DriverStateException {

    public InvalidDriverTransitionException(
            String currentState,
            String requestedAction
    ) {
        super(
            "Cannot perform action '%s' while driver is in state '%s'"
                .formatted(requestedAction, currentState)
        );
    }
}