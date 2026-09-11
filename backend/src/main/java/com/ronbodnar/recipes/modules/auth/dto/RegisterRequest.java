package com.ronbodnar.recipes.modules.auth.dto;

import jakarta.validation.constraints.*;

import java.util.UUID;

public record RegisterRequest(
        @NotBlank
        @Size(min = 3, max = 20)
        @Pattern(
                regexp = "^[a-zA-Z0-9._]+$",
                message = "Username may only contain letters, numbers, periods, and underscores."
        )
        String username,

        @NotBlank
        @Size(min = 8, max = 50)
        String password,

        @NotBlank
        @Size(min = 8, max = 50)
        String confirmPassword,

        @Size(min = 2, max = 30)
        String givenName,

        @Size(min = 2, max = 30)
        String familyName,

        @NotBlank
        @Email
        String email,

        @NotNull
        UUID deviceId
) {}