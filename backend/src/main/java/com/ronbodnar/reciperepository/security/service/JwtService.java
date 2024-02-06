package com.ronbodnar.reciperepository.security.service;

import java.security.Key;
import java.util.Date;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtService {

    private static final Logger logger = LoggerFactory.getLogger(JwtService.class);

    private final String COOKIE_NAME = "recipe-repo";

    @Value("${com.ronbodnar.reciperepository.secret}")
    private String secret;

    public String getFromCookies(HttpServletRequest request) {
        Cookie cookie = WebUtils.getCookie(request, COOKIE_NAME);
        if (cookie == null)
            return null;

        return cookie.getValue();
    }

    public ResponseCookie generateDefaultCookie() {
        return ResponseCookie.from(COOKIE_NAME, "").path("/").maxAge(0).httpOnly(true).build();
    }

    public ResponseCookie generateCookie(UserDetailsImpl userPrincipal) {
        String jwt = generateTokenFromUsername(userPrincipal.getUsername());
        return ResponseCookie.from(COOKIE_NAME, jwt).path("/").maxAge(24 * 60 * 60).httpOnly(true).build();
    }

    public String getUsername(String token) {
        return Jwts.parserBuilder().setSigningKey(getKey()).build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validate(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(getKey()).build().parse(authToken);
            return true;
        } catch (MalformedJwtException|ExpiredJwtException|UnsupportedJwtException|IllegalArgumentException e) {
            logger.error("JWT Exception: {}", e.getMessage());
        }
        return false;
    }

    public String generateTokenFromUsername(String username) {
        int expiration = 86400000;
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + expiration))
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key getKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }
}