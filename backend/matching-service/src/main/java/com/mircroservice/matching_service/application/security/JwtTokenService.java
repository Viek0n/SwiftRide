package com.mircroservice.matching_service.application.security;

import java.time.Instant;
import java.util.Base64;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.mircroservice.matching_service.domain.entities.User;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtTokenService {
    private final SecretKey signingKey;
    private final long expirationSeconds;

    public JwtTokenService(
        @Value("${jwt.secret-base64}") String secretBase64,
        @Value("${jwt.expiration-seconds:3600}") long expirationSeconds
    ) {
        byte[] secret = Base64.getDecoder().decode(secretBase64);
        this.signingKey = Keys.hmacShaKeyFor(secret);
        if (expirationSeconds <= 0) {
            throw new IllegalArgumentException("JWT expiration must be greater than zero");
        }
        this.expirationSeconds = expirationSeconds;
    }

    public String createToken(User user) {
        Instant issuedAt = Instant.now();
        return Jwts.builder()
            .subject(user.getId())
            .claim("role", "USER")
            .issuedAt(Date.from(issuedAt))
            .expiration(Date.from(issuedAt.plusSeconds(expirationSeconds)))
            .signWith(signingKey, Jwts.SIG.HS256)
            .compact();
    }
}
