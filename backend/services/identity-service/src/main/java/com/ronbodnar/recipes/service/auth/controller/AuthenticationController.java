package com.ronbodnar.recipes.service.auth.controller;

import com.ronbodnar.recipes.service.auth.service.AuthenticationService;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/identity")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @GetMapping("/me")
    public String getAuthenticatedUser(@AuthenticationPrincipal Jwt jwt) {
        return jwt == null ? null : jwt.getSubject();
    }
}