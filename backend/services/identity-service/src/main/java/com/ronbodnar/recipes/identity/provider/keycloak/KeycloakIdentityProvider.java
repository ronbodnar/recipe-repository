package com.ronbodnar.recipes.identity.provider.keycloak;

import com.ronbodnar.recipes.common.exception.BusinessException;
import com.ronbodnar.recipes.common.exception.ErrorCode;
import com.ronbodnar.recipes.identity.domain.IdentityUser;
import com.ronbodnar.recipes.identity.provider.IdentityProvider;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;

import static org.springframework.security.oauth2.client.web.ClientAttributes.clientRegistrationId;

@Service
public class KeycloakIdentityProvider implements IdentityProvider {

    @Value("${APP_KEYCLOAK_URI}")
    private String keycloakUri;

    @Value("${APP_KEYCLOAK_REALM}")
    private String keycloakRealm;

    private final RestClient restClient;

    public KeycloakIdentityProvider(RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public IdentityUser fromToken(Jwt jwt) {
        return new IdentityUser(
                jwt.getSubject(),
                jwt.getClaimAsString("preferred_username"),
                jwt.getClaimAsString("given_name"),
                jwt.getClaimAsString("family_name"),
                jwt.getClaimAsString("email"),
                Boolean.TRUE.equals(jwt.getClaimAsBoolean("email_verified"))
        );
    }

    @Override
    public void updateUser(IdentityUser updatedUser) {
        // https://www.keycloak.org/docs-api/latest/rest-api/index.html#_put_adminrealmsrealmusersuser_id
        System.out.println("Received request to update Keycloak user (" + updatedUser.subject() + "): " + updatedUser.subject());

        KeycloakUserRepresentation userRepresentation = KeycloakUserRepresentation.from(updatedUser);

        try {
            restClient.put()
                    .uri(keycloakUri + "/admin/realms/" + keycloakRealm + "/users/" + updatedUser.subject())
                    .attributes(clientRegistrationId("keycloak"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(userRepresentation)
                    .retrieve()
                    .toBodilessEntity();
        } catch (HttpClientErrorException.Conflict e) {
            KeycloakErrorResponse error = e.getResponseBodyAs(
                    KeycloakErrorResponse.class
            );

            if (error == null) {
                throw new BusinessException(ErrorCode.USER_PROFILE_CONFLICT);
            }

            if ("User exists with same email".equals(error.errorMessage())) {
                throw new BusinessException(ErrorCode.EMAIL_ALREADY_IN_USE, "email", "The email address is already in use.");
            }

            throw new BusinessException(ErrorCode.USER_PROFILE_CONFLICT);
        } catch (HttpClientErrorException.BadRequest e) {
            System.out.println("Bad Request exception: " + e.getResponseBodyAsString());
            throw new BusinessException(ErrorCode.VALIDATION_FAILED);
        } catch (HttpClientErrorException.Forbidden e) {
            System.out.println("Forbidden exception: " + e.getResponseBodyAsString());
            throw new BusinessException(ErrorCode.NOT_AUTHORIZED);
        } catch (HttpServerErrorException.InternalServerError e) {
            System.out.println("Internal server error: " + e.getResponseBodyAsString());
            throw new BusinessException(ErrorCode.INTERNAL_ERROR);
        }
    }

}