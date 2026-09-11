package com.ronbodnar.recipes.exception;

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
    INCORRECT_PARAMETER(HttpStatus.BAD_REQUEST, LogLevel.WARN),

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
    INVALID_AUTHENTICATION(HttpStatus.UNAUTHORIZED, LogLevel.ERROR),

    // Recipes
    RECIPE_NOT_FOUND(HttpStatus.NOT_FOUND,LogLevel.INFO),
    DUPLICATE_RECIPE(HttpStatus.CONFLICT, LogLevel.INFO),
    RECIPE_MISMATCH(HttpStatus.BAD_REQUEST, LogLevel.WARN),

    // Images
    ATTACH_IMAGES_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, LogLevel.WARN),
    MARK_IMAGES_FOR_DELETION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, LogLevel.WARN),
    IMAGE_UPLOAD_FAILED(HttpStatus.BAD_REQUEST, LogLevel.ERROR),
    IMAGE_SIZE_TOO_BIG(HttpStatus.BAD_REQUEST, LogLevel.WARN),
    UNSUPPORTED_MEDIA_TYPE(HttpStatus.UNSUPPORTED_MEDIA_TYPE, LogLevel.WARN),

    // Users
    USER_NOT_FOUND(HttpStatus.NOT_FOUND,LogLevel.WARN),
    USER_PROFILE_CONFLICT(HttpStatus.CONFLICT, LogLevel.WARN),
    EMAIL_ALREADY_IN_USE(HttpStatus.CONFLICT, LogLevel.INFO),
    USERNAME_ALREADY_IN_USE(HttpStatus.CONFLICT, LogLevel.INFO),
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
