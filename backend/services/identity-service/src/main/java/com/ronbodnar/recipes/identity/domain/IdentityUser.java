package com.ronbodnar.recipes.identity.domain;

import com.ronbodnar.recipes.user.dto.UserAccountChangeRequest;

public record IdentityUser(
        String subject,
        String username,
        String givenName,
        String familyName,
        String email,
        boolean emailVerified
) {
    public IdentityUser withChanges(UserAccountChangeRequest request) {
        boolean emailChanged = !request.email().equalsIgnoreCase(email());

        return new IdentityUser(
                subject(),
                username(),
                request.givenName(),
                request.familyName(),
                request.email(),
                emailVerified() && !emailChanged
        );
    }
}