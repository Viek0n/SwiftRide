package com.mircroservice.matching_service.application.security;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Base64;

import javax.crypto.SecretKey;

import org.junit.jupiter.api.Test;

import com.mircroservice.matching_service.domain.entities.User;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

class JwtTokenServiceTest {
    private static final byte[] SECRET = new byte[32];

    @Test
    void createsSignedTokenWithUserSubjectAndRole() {
        String secretBase64 = Base64.getEncoder().encodeToString(SECRET);
        JwtTokenService tokenService = new JwtTokenService(secretBase64, 3600);
        User user = new User();
        user.setId("user-123");

        String token = tokenService.createToken(user);
        SecretKey verificationKey = Keys.hmacShaKeyFor(SECRET);
        var claims = Jwts.parser()
            .verifyWith(verificationKey)
            .build()
            .parseSignedClaims(token)
            .getPayload();

        assertThat(claims.getSubject()).isEqualTo("user-123");
        assertThat(claims.get("role", String.class)).isEqualTo("USER");
        assertThat(claims.getExpiration()).isAfter(claims.getIssuedAt());
    }
}
