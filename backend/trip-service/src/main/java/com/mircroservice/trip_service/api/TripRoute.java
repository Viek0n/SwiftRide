package com.mircroservice.trip_service.api;

public class TripRoute {
    public static final String BASE = "api/trips";

    public static final String CREATE = "";
    public static final String GET = "/{tripId}";

    public static final String ACCEPT = "/{tripId}/driver-accept";
    public static final String REJECT = "/{tripId}/driver-reject";
    public static final String EXPIRE = "/{tripId}/expire";
    
    public static final String PICKEDUP = "/{tripId}/pickedup";
    public static final String COMPLETE = "/{tripId}/complete";

    public TripRoute(){
        
    }
}
