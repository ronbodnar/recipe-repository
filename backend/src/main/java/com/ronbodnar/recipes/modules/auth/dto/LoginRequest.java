package com.ronbodnar.recipes.modules.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record LoginRequest(
        @NotBlank
        @Size(min = 3, max = 20)
        String username,

        @NotBlank
        @Size(min = 8, max = 50)
        String password,

        @NotNull
        UUID deviceId
) {}
