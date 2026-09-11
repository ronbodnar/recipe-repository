package com.ronbodnar.recipes.exception.payload;

import com.fasterxml.jackson.annotation.JsonIgnore;

import com.ronbodnar.recipes.exception.ErrorCode;

public record FieldError(
        ErrorCode errorCode,
        String field,
        @JsonIgnore String logMessage
) {

    public FieldError(ErrorCode errorCode) {
        this(errorCode, null, null);
    }

    public FieldError(ErrorCode errorCode, String logMessage) {
        this(errorCode, null, logMessage);
    }

}