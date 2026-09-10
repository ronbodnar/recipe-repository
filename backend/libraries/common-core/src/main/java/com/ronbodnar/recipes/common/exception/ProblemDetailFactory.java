package com.ronbodnar.recipes.common.exception;

import com.ronbodnar.recipes.common.exception.payload.FieldError;
import org.springframework.http.ProblemDetail;

import java.net.URI;
import java.time.Instant;
import java.util.List;

public class ProblemDetailFactory {

    public static ProblemDetail create(ErrorCode errorCode) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(errorCode.getHttpStatus());
        problemDetail.setProperty("code", errorCode.name());
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }

    public static ProblemDetail create(ErrorCode errorCode, String detail) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(errorCode.getHttpStatus(), detail);
        problemDetail.setType(URI.create("errors:" + errorCode.name()));
        problemDetail.setProperty("code", errorCode.name());
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }

    public static ProblemDetail create(ErrorCode errorCode, String detail, List<FieldError> fieldErrors) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(errorCode.getHttpStatus(), detail);
        problemDetail.setType(URI.create("errors:" + errorCode.name()));
        problemDetail.setProperty("code", errorCode.name());
        problemDetail.setProperty("fieldErrors", fieldErrors);
        problemDetail.setProperty("timestamp", Instant.now());

        return problemDetail;
    }

    public static ProblemDetail validation(List<FieldError> fieldErrors) {
        ProblemDetail problemDetail = create(ErrorCode.VALIDATION_FAILED);
        problemDetail.setProperty("fieldErrors", fieldErrors);
        return problemDetail;
    }
}
