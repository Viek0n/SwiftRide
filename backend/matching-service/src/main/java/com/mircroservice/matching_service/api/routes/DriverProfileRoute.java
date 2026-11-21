package com.mircroservice.matching_service.api.routes;

public class DriverProfileRoute {
    public static final String BASE = "drivers";

    public static final String CREATE = "";
    public static final String UPDATE = "/{userId}";
    public static final String DELETE = "/{userId}";

    public static final String LIST = "";
    public static final String PROFILE = "/{userId}";
    public static final String SOFT_DELETE = "/{userId}/soft-delete";

    private DriverProfileRoute(){

    }
}
