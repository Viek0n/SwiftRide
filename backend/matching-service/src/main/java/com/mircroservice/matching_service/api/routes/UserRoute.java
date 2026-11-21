package com.mircroservice.matching_service.api.routes;

public class UserRoute {
    public static final String BASE = "api/users";
    
    public static final String CREATE = "";
    public static final String UPDATE = "/{id}";
    public static final String DELETE = "/{id}";

    public static final String LIST = "";
    public static final String PROFILE = "/{id}";
    public static final String SOFT_DELETE = "/{id}/soft-delete";

    private UserRoute(){

    }
}
