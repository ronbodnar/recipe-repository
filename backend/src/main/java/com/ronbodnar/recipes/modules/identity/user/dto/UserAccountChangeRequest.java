package com.ronbodnar.recipes.modules.identity.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record UserAccountChangeRequest(
        @NotBlank @Size(min = 2, max = 50) String username,
        @NotBlank @Size(min = 2, max = 50) String givenName,
        @NotBlank @Size(min = 2, max = 50) String familyName,
        @NotBlank @Email String email,
        UUID profileImageId
) {}