package com.ronbodnar.recipes.security.jwt;

import com.ronbodnar.recipes.exception.BusinessException;
import com.ronbodnar.recipes.exception.ErrorCode;
import com.ronbodnar.recipes.modules.auth.domain.AuthenticationTokenType;
import com.ronbodnar.recipes.security.adapter.SecurityUserDetails;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;

import java.security.Key;
import java.util.*;

@Slf4j
@Component
public class JwtService {

    private final String secret;

    public JwtService(@Value("${app.security.jwt-secret}") String secret) {
        this.secret = secret;
    }

    public Claims getClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getKeyWithSecret())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException | IllegalArgumentException ignored) {
            return null;
        }
    }

    public boolean isInvalid(String token) {
        if (token == null) {
            return true;
        }
        try {
            Jwts.parser()
                    .verifyWith(getKeyWithSecret())
                    .build()
                    .parseSignedClaims(token);

            return false;
        } catch (JwtException | IllegalArgumentException e) {
            String errorName = e.getClass().getSimpleName();
            if (!errorName.equals("ExpiredJwtException")) {
                log.warn("Invalid JWT: {} (truncated token: {})", e.getClass().getSimpleName(), token.isEmpty() ? "" : token.substring(0, 6));
            }
        }
        return true;
    }

    public String generateAuthToken(
            AuthenticationTokenType tokenType,
            UUID deviceId,
            SecurityUserDetails userPrincipal,
            long expiration
    ) {
        if (userPrincipal == null) {
            throw new BusinessException(ErrorCode.INVALID_ACCESS_TOKEN, "User Principal not found for %s token request".formatted(tokenType.name()));
        }

        Map<String, Object> claims = new HashMap<>(Map.of(
                "id", userPrincipal.getId(),
                "deviceId", deviceId
        ));

        if (tokenType == AuthenticationTokenType.ACCESS) {
            claims.put("username", userPrincipal.getUsername());
            claims.put("roles", userPrincipal.getAuthorities());
        }

        String subject = userPrincipal.getUsername();

        return generateToken(subject, expiration, claims);
    }

    private String generateToken(
            String subject,
            long expiration,
            Map<String, ?> claims
    ) {
        log.debug("Generating JWT expiring in {}ms for {} with claims {}", expiration, subject, claims);

        Key signingKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));

        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(new Date())
                .expiration(new Date((new Date()).getTime() + expiration))
                .signWith(signingKey)
                .compact();
    }

    private SecretKey getKeyWithSecret() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }

}