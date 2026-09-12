package com.ronbodnar.recipes.modules.identity.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record UserAccountChangeRequest(
        @NotBlank @Size(min = 3, max = 30) String username,
        @Size(min = 2, max = 150) String givenName,
        @Size(min = 2, max = 150) String familyName,
        @NotBlank @Email @Size(max = 254) String email,
        UUID profileImageId
) {}