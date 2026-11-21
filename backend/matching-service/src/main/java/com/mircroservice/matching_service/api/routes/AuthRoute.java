package com.mircroservice.matching_service.api.routes;

public class AuthRoute {
    public static final String BASE = "auth";

    public static final String LOGIN = "/login";
    public static final String LOGIN_USER = LOGIN + "/user";
    public static final String LOGIN_RIDER = LOGIN + "/rider";

    public static final String LOGOUT = "/logout";
    public static final String REFRESH_TOKEN = "/refresh";
    
    public static final String REGISTER = "/register";
    public static final String REGISTER_USER = REGISTER + "/user";
    public static final String REGISTER_RIDER = REGISTER + "/rider";

    private AuthRoute(){

    }
}
