package com.ronbodnar.recipes.security.jwt;

import com.ronbodnar.recipes.modules.auth.domain.AuthenticationTokenType;
import com.ronbodnar.recipes.security.adapter.SecurityUserDetails;
import com.ronbodnar.recipes.modules.auth.domain.TokenPair;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import org.springframework.web.util.WebUtils;

import java.util.UUID;

@Service
public class JwtCookieService {

    @Value("${spring.profiles.active:default}")
    private String activeProfile;

    private final JwtService jwtService;

    private static final String ACCESS_COOKIE_NAME = "app_access";
    private static final String REFRESH_COOKIE_NAME = "app_refresh";

    private static final long ACCESS_COOKIE_EXPIRATION = 1000 * 60 * 5; // 5 minutes
    private static final long REFRESH_COOKIE_EXPIRATION = 1000 * 60 * 60 * 24 * 7; // 7 days

    public JwtCookieService(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    public String getTokenFromRequest(HttpServletRequest request, AuthenticationTokenType tokenType) {
        Cookie cookie = WebUtils.getCookie(request, getCookieName(tokenType));
        if (cookie == null)
            return null;

        return cookie.getValue();
    }

    public HttpHeaders createCookieHeaders(TokenPair tokenPair) {
        ResponseCookie accessCookie = buildCookie(
                AuthenticationTokenType.ACCESS,
                tokenPair.accessToken(),
                ACCESS_COOKIE_EXPIRATION
        );

        ResponseCookie refreshCookie = buildCookie(
                AuthenticationTokenType.REFRESH,
                tokenPair.refreshToken(),
                REFRESH_COOKIE_EXPIRATION
        );

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, accessCookie.toString());
        headers.add(HttpHeaders.SET_COOKIE, refreshCookie.toString());

        return headers;
    }

    public ResponseCookie createJwtCookie(
            AuthenticationTokenType tokenType,
            UUID deviceId,
            SecurityUserDetails userPrincipal
    ) {
        if (userPrincipal == null) {
            return buildCookie(tokenType, "", 0);
        }

        long expiration = getCookieExpiration(tokenType);

        String jwt = jwtService.generateAuthToken(tokenType, deviceId, userPrincipal, expiration);

        return buildCookie(tokenType, jwt, expiration);
    }

    private ResponseCookie buildCookie(AuthenticationTokenType tokenType, String jwt, long maxAgeSeconds) {
        String cookieName = getCookieName(tokenType);
        boolean isProdEnvironment = activeProfile.startsWith("prod");

        return ResponseCookie
                .from(cookieName, jwt)
                .path("/")
                .maxAge(maxAgeSeconds)
                .secure(isProdEnvironment)
                .httpOnly(true)
                .sameSite("Strict")
                .build();
    }

    public static String getCookieName(AuthenticationTokenType tokenType) {
        return switch (tokenType) {
            case ACCESS -> ACCESS_COOKIE_NAME;
            case REFRESH -> REFRESH_COOKIE_NAME;
        };
    }

    public static long getCookieExpiration(AuthenticationTokenType tokenType) {
        return switch (tokenType) {
            case ACCESS -> ACCESS_COOKIE_EXPIRATION;
            case REFRESH -> REFRESH_COOKIE_EXPIRATION;
        };
    }
}
