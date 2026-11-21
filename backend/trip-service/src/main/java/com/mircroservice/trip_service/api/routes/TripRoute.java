package com.mircroservice.trip_service.api.routes;

public class TripRoute {
    public static final String BASE = "trips";

    public static final String CREATE = "";
    public static final String GET = "/{id}";

    public static final String ACCEPT = "/{id}/driver-accept";
    public static final String REJECT = "/{id}/driver-reject";
    public static final String EXPIRE = "/{id}/expire";
    
    public static final String PICKUP = "/{id}/pickup";
    public static final String DROPOFF = "/{id}/dropoff";

    public TripRoute(){
        
    }
}
