package com.ronbodnar.recipes.common.exception;

import lombok.Getter;
import org.springframework.boot.logging.LogLevel;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    // Internals
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, LogLevel.ERROR),
    MESSAGE_NOT_READABLE(HttpStatus.INTERNAL_SERVER_ERROR, LogLevel.ERROR),
    MESSAGE_NOT_WRITABLE(HttpStatus.BAD_REQUEST, LogLevel.ERROR),
    VALIDATION_FAILED(HttpStatus.BAD_REQUEST, LogLevel.INFO),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, LogLevel.INFO),
    MISSING_PARAMETERS(HttpStatus.BAD_REQUEST, LogLevel.WARN),

    // Validation
    FIELD_MIN_LENGTH(HttpStatus.BAD_REQUEST, LogLevel.INFO),
    FIELD_MAX_LENGTH(HttpStatus.BAD_REQUEST, LogLevel.INFO),
    FIELD_MIN_VALUE(HttpStatus.BAD_REQUEST, LogLevel.INFO),
    FIELD_MAX_VALUE(HttpStatus.BAD_REQUEST, LogLevel.INFO),
    FIELD_REQUIRED(HttpStatus.BAD_REQUEST, LogLevel.INFO),
    FIELD_EMAIL(HttpStatus.BAD_REQUEST, LogLevel.INFO),
    FIELD_PATTERN(HttpStatus.BAD_REQUEST, LogLevel.INFO),

    // Authentication
    NOT_AUTHORIZED(HttpStatus.FORBIDDEN, LogLevel.WARN),
    AUTH_USER_LOCKED(HttpStatus.FORBIDDEN, LogLevel.WARN),
    AUTH_USER_BLOCKED(HttpStatus.UNAUTHORIZED, LogLevel.WARN),
    AUTH_USER_NOT_FOUND(HttpStatus.BAD_REQUEST, LogLevel.WARN),
    ROLE_NOT_FOUND(HttpStatus.NOT_FOUND,LogLevel.WARN),
    INVALID_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, LogLevel.INFO),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, LogLevel.INFO),
    INVALID_CREDENTIALS(HttpStatus.BAD_REQUEST, LogLevel.INFO),

    // Recipes
    RECIPE_NOT_FOUND(HttpStatus.NOT_FOUND,LogLevel.INFO),
    DUPLICATE_RECIPE(HttpStatus.CONFLICT, LogLevel.INFO),
    RECIPE_MISMATCH(HttpStatus.BAD_REQUEST, LogLevel.WARN),

    // Images
    IMAGE_UPLOAD_FAILED(HttpStatus.BAD_REQUEST, LogLevel.ERROR),
    IMAGE_SIZE_TOO_BIG(HttpStatus.BAD_REQUEST, LogLevel.WARN),
    UNSUPPORTED_MEDIA_TYPE(HttpStatus.UNSUPPORTED_MEDIA_TYPE, LogLevel.WARN),

    // Users
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, LogLevel.WARN),
    DUPLICATE_USERNAME(HttpStatus.CONFLICT, LogLevel.WARN),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, LogLevel.INFO),
    PASSWORD_MISMATCH(HttpStatus.BAD_REQUEST, LogLevel.INFO),
    INCORRECT_PASSWORD(HttpStatus.BAD_REQUEST, LogLevel.INFO),
    INCORRECT_NEW_PASSWORD(HttpStatus.BAD_REQUEST, LogLevel.INFO);

    private final HttpStatus httpStatus;
    private final LogLevel logLevel;

    ErrorCode(HttpStatus httpStatus, LogLevel logLevel) {
        this.httpStatus = httpStatus;
        this.logLevel = logLevel;
    }

}
