package com.mircroservice.trip_service.api.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

public final class AuthenticatedUser {

    private AuthenticatedUser() {}

    public static String getUserId() {
        Authentication authentication =
            SecurityContextHolder.getContext().getAuthentication();

        if (!(authentication instanceof JwtAuthenticationToken jwtAuth)) {
            throw new IllegalStateException("User is not authenticated");
        }

        return jwtAuth.getToken().getSubject();
    }
}