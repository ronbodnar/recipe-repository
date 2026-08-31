package com.ronbodnar.recipes.identity;

import com.ronbodnar.recipes.common.exception.BusinessException;
import com.ronbodnar.recipes.common.exception.ErrorCode;
import com.ronbodnar.recipes.identity.dto.KeycloakUserRepresentation;
import com.ronbodnar.recipes.user.dto.UserAccountDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.Objects;
import java.util.UUID;

import static org.springframework.security.oauth2.client.web.ClientAttributes.clientRegistrationId;

@Service
public class KeycloakUserService {

    @Value("${APP_KEYCLOAK_URI}")
    private String keycloakUri;

    @Value("${APP_KEYCLOAK_REALM}")
    private String keycloakRealm;

    private final RestClient restClient;

    public KeycloakUserService(RestClient restClient) {
        this.restClient = restClient;
    }

    // https://www.keycloak.org/docs-api/latest/rest-api/index.html#_users
    // PUT /admin/realms/{realm}/users/{user-id}
    public void updateKeycloakUser(KeycloakUserRepresentation userRepresentation, Jwt jwt) {
        UUID userId = UUID.fromString(Objects.requireNonNull(jwt.getSubject(), "JWT is missing subject"));

        System.out.println("Received request to update keycloak user (" + userId + "): " + userRepresentation);

        try {
            ResponseEntity<Void> response = restClient.put()
                    .uri(keycloakUri + "/admin/realms/" + keycloakRealm + "/users/" + userId)
                    .attributes(clientRegistrationId("keycloak"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(userRepresentation)
                    .retrieve()
                    .toBodilessEntity();
        } catch (HttpClientErrorException.Conflict e) {
            KeycloakErrorResponse error = e.getResponseBodyAs(
                    KeycloakErrorResponse.class
            );

            if ("User exists with same email".equals(error.errorMessage())) {
                throw new BusinessException(
                        ErrorCode.EMAIL_ALREADY_IN_USE,
                        "email",
                        "That email address is already in use."
                );
            }

            if ("User exists with same username".equals(error.errorMessage())) {
                throw new BusinessException(
                        ErrorCode.USERNAME_ALREADY_IN_USE,
                        "username",
                        "That username is already in use."
                );
            }

            throw new BusinessException(ErrorCode.USER_PROFILE_CONFLICT);
        }
    }

    private OAuth2AccessToken getAccessToken(@RegisteredOAuth2AuthorizedClient("keycloak-admin") OAuth2AuthorizedClient authorizedClient) {
        return authorizedClient.getAccessToken();
    }
}
