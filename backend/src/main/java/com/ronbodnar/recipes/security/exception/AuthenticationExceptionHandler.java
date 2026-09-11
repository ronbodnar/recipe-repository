package com.ronbodnar.recipes.security.exception;

import com.ronbodnar.recipes.exception.ErrorCode;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.jspecify.annotations.NonNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Component
public class AuthenticationExceptionHandler implements AuthenticationEntryPoint {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationExceptionHandler.class);

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, @NonNull AuthenticationException authException) throws IOException {
        logger.warn("Unauthorized request to {} from {}", request.getRequestURI(), request.getRemoteAddr());

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        final Map<String, Object> body = new HashMap<>();
        body.put("status", HttpServletResponse.SC_UNAUTHORIZED);
        body.put("timestamp", Instant.now().toString());
        body.put("path", request.getRequestURI());
        body.put("code", ErrorCode.INVALID_ACCESS_TOKEN);

        final ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getOutputStream(), body);
    }

}