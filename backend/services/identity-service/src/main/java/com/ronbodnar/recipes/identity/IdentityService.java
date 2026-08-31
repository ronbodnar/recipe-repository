package com.ronbodnar.recipes.identity;

import com.ronbodnar.recipes.identity.dto.KeycloakUserRepresentation;
import com.ronbodnar.recipes.user.UserAccountService;
import com.ronbodnar.recipes.user.dto.UserAccountDTO;

import lombok.extern.slf4j.Slf4j;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class IdentityService {

    private final UserAccountService userAccountService;
    private final KeycloakUserService keycloakUserService;

    public IdentityService(UserAccountService userAccountService, KeycloakUserService keycloakUserService) {
        this.userAccountService = userAccountService;
        this.keycloakUserService = keycloakUserService;
    }

    public UserAccountDTO getAuthenticatedUser(Jwt jwt) {
        return userAccountService.getOrCreateUserAccount(jwt);
    }

    public void updateAuthenticatedUser(KeycloakUserRepresentation userRepresentation, Jwt jwt) {
        log.info("Received request to update authenticated user: {}", userRepresentation);
        keycloakUserService.updateKeycloakUser(userRepresentation, jwt);
    }
}