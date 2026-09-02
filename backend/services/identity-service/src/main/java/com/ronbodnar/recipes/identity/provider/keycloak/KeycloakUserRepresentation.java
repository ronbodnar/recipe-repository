package com.ronbodnar.recipes.identity.provider.keycloak;

import com.ronbodnar.recipes.identity.domain.IdentityUser;

public record KeycloakUserRepresentation(
        String id,
        String username,
        String firstName,
        String lastName,
        String email,
        boolean emailVerified
) {

    public static KeycloakUserRepresentation from(IdentityUser identityUser) {
        return new KeycloakUserRepresentation(
                identityUser.subject(),
                identityUser.username(),
                identityUser.givenName(),
                identityUser.familyName(),
                identityUser.email(),
                identityUser.emailVerified()
        );
    }

}