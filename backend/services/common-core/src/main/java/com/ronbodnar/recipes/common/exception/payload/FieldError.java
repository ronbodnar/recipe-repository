package com.ronbodnar.recipes.common.exception.payload;

import com.fasterxml.jackson.annotation.JsonIgnore;

import com.ronbodnar.recipes.common.exception.ErrorCode;

import jakarta.validation.constraints.NotNull;

public record FieldError(
        ErrorCode errorCode,
        String field,
        @JsonIgnore String logMessage
) {}