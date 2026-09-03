package com.ronbodnar.recipes.common.exception;

import com.ronbodnar.recipes.common.exception.payload.FieldError;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;

import lombok.extern.slf4j.Slf4j;

import org.apache.tomcat.util.http.fileupload.impl.InvalidContentTypeException;

import org.springframework.boot.logging.LogLevel;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ProblemDetail handleHttpMessageNotReadableException(HttpMessageNotReadableException ex, HttpServletRequest request) {
        log.info("Message unreadable: {}", ex.getMessage());
        return ProblemDetailFactory.create(ErrorCode.MESSAGE_NOT_READABLE);
    }

    @ExceptionHandler(HttpMessageNotWritableException.class)
    public ProblemDetail handleHttpMessageNotWritable(HttpMessageNotWritableException ex, HttpServletRequest request) {
        log.info("Message unwritable: {}", ex.getMessage());
        return ProblemDetailFactory.create(ErrorCode.MESSAGE_NOT_WRITABLE);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ProblemDetail handleValidationExceptions(ConstraintViolationException ex, HttpServletRequest request) {
        List<FieldError> errors = new ArrayList<>();
        ex.getConstraintViolations().forEach((violation) -> {
            errors.add(new FieldError(ErrorCode.VALIDATION_FAILED, violation.getPropertyPath().toString(), violation.getMessage()));
        });
        return ProblemDetailFactory.validation(errors);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ProblemDetail handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        return ProblemDetailFactory.create(ErrorCode.METHOD_NOT_ALLOWED, "Method not allowed: " + ex.getMethod());
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGenericException(Exception ex, HttpServletRequest request) {
        ex.printStackTrace(); // Log for debugging
        return ProblemDetailFactory.create(ErrorCode.INTERNAL_ERROR);
    }

    @ExceptionHandler(BusinessException.class)
    public ProblemDetail handleBusinessException(BusinessException ex, HttpServletRequest request) {
        //TODO: implement authentication to get the userId
        Long userId = 0L;

        LogLevel level = ex.getErrorCode().getLogLevel();
        switch(level) {
            case ERROR -> log.error("User {} at {}: {}", userId, request.getRequestURI(), ex.getMessage());
            case WARN -> log.warn("User {} at {}: {}", userId, request.getRequestURI(), ex.getMessage());
            case INFO -> log.info("User {} at {}: {}", userId, request.getRequestURI(), ex.getMessage());
            default -> log.debug("User {} at {}: {}", userId, request.getRequestURI(), ex.getMessage());
        }

        return ProblemDetailFactory.create(ex.getErrorCode(), ex.getDetail(), ex.getFieldErrors());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationExceptions(MethodArgumentNotValidException ex, HttpServletRequest request) {
        List<FieldError> errors = new ArrayList<>();
        ex.getBindingResult().getFieldErrors().forEach(fieldError -> {
            ErrorCode errorCode = mapValidationError(fieldError);
            errors.add(new FieldError(errorCode, fieldError.getField(), fieldError.getDefaultMessage()));
        });
        return ProblemDetailFactory.validation(errors);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ProblemDetail handleMaxUploadSizeExceededException(MaxUploadSizeExceededException ex, HttpServletRequest request) {
        return ProblemDetailFactory.create(ErrorCode.IMAGE_SIZE_TOO_BIG);
    }


    @ExceptionHandler(InvalidContentTypeException.class)
    public ProblemDetail handleInvalidContentTypeException(InvalidContentTypeException ex) {
        log.warn("Unsupported media type: {}", ex.getMessage());
        return ProblemDetailFactory.create(ErrorCode.UNSUPPORTED_MEDIA_TYPE, ex.getMessage());
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ProblemDetail handleAuthorizationDeniedException(AuthorizationDeniedException ex) {
        log.info("Unauthorized access to resource: {}", ex.getMessage());
        return ProblemDetailFactory.create(ErrorCode.NOT_AUTHORIZED, ex.getMessage());
    }

    private ErrorCode mapValidationError(org.springframework.validation.FieldError fieldError) {
        String code = fieldError.getCode();
        if (code == null) return ErrorCode.VALIDATION_FAILED;

        switch (code) {
            case "NotNull":
            case "NotBlank":
            case "NotEmpty":
                return ErrorCode.FIELD_REQUIRED;

            case "Size":
            case "Length":
                Object[] args = fieldError.getArguments();
                if (args != null && args.length >= 3) {
                    int rejectedLength = getRejectedStringLength(fieldError.getRejectedValue());
                    int max = (Integer) args[1];
                    int min = (Integer) args[2];
                    if (rejectedLength < min) return ErrorCode.FIELD_MIN_LENGTH;
                    if (rejectedLength > max) return ErrorCode.FIELD_MAX_LENGTH;
                }
                return ErrorCode.FIELD_MIN_LENGTH;

            case "Min":
            case "DecimalMin":
                return ErrorCode.FIELD_MIN_VALUE;

            case "Max":
            case "DecimalMax":
                return ErrorCode.FIELD_MAX_VALUE;

            case "Email":
                return ErrorCode.FIELD_EMAIL;

            case "Pattern":
                return ErrorCode.FIELD_PATTERN;

            default:
                return ErrorCode.VALIDATION_FAILED;
        }
    }

    private int getRejectedStringLength(Object rejectedValue) {
        if (rejectedValue == null) return 0;
        if (rejectedValue instanceof DefaultMessageSourceResolvable d) return d.toString().length();
        return rejectedValue.toString().length();
    }

}
