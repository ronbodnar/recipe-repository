package com.ronbodnar.recipes.security.filter;

import com.ronbodnar.recipes.security.jwt.JwtAuthenticationMapper;
import com.ronbodnar.recipes.security.jwt.JwtCookieService;
import com.ronbodnar.recipes.security.jwt.JwtService;
import com.ronbodnar.recipes.modules.auth.domain.AuthenticationTokenType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.jspecify.annotations.NonNull;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final JwtCookieService jwtCookieService;

    public JwtAuthenticationFilter(JwtService jwtService, JwtCookieService jwtCookieService) {
        this.jwtService = jwtService;
        this.jwtCookieService = jwtCookieService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        String accessToken = jwtCookieService.getTokenFromRequest(
                request,
                AuthenticationTokenType.ACCESS
        );
        if (accessToken != null) {
            Claims claims = null;
            try {
                claims = jwtService.getClaims(accessToken);
            } catch (JwtException | IllegalArgumentException ignored) {}

            if (claims != null) {
                if (jwtService.isInvalid(accessToken)) {
                    String deviceIdRaw = claims.get("deviceId", String.class);
                    UUID deviceId = deviceIdRaw == null ? null : UUID.fromString(deviceIdRaw);
                    ResponseCookie expiredToken = jwtCookieService.createJwtCookie(
                            AuthenticationTokenType.ACCESS,
                            deviceId,
                            null
                    );
                    response.addHeader(HttpHeaders.SET_COOKIE, expiredToken.toString());
                } else {
                    UserDetails userDetails = JwtAuthenticationMapper.toUserDetails(claims);
                    updateSecurityContextAuthentication(request, userDetails);
                }
            }
        }

        filterChain.doFilter(request, response);
    }

    public void updateSecurityContextAuthentication(HttpServletRequest request, UserDetails userDetails) {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities());

        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    /*
     * Since we're using DI and this Filter is a Spring Bean,
     * tell Spring Boot not to register it to the container.
     */
    @Bean
    public FilterRegistrationBean<JwtAuthenticationFilter> jwtAuthenticationFilterRegistration(
            JwtAuthenticationFilter filter
    ) {
        FilterRegistrationBean<JwtAuthenticationFilter> registration = new FilterRegistrationBean<>(
                filter
        );
        registration.setEnabled(false);
        return registration;
    }

}