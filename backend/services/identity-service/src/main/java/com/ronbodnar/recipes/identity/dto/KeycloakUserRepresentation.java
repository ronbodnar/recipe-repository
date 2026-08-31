package com.ronbodnar.recipes.identity.dto;

public record KeycloakUserRepresentation(
        String username,
        String firstName,
        String lastName,
        String email
) {}