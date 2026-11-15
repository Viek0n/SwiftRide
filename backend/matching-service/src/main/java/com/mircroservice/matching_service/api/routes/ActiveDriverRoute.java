package com.mircroservice.matching_service.api.routes;

public class ActiveDriverRoute {
    public static final String ACTIVE_DRIVER_CONTROLLER = "/active-drivers";
    public static final String LOCATION = "/{driverId}/location";
    public static final String ACCEPT = "/{driverId}/accept";
    public static final String REJECT = "/{driverId}/reject";

    public ActiveDriverRoute(){
        
    }
}
