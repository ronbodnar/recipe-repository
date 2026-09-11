package com.ronbodnar.recipes.exception;

import com.ronbodnar.recipes.exception.payload.FieldError;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class BusinessException extends RuntimeException {

    private String detail;
    private ErrorCode errorCode;

    private List<FieldError> fieldErrors = new ArrayList<>();

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.name());
        this.errorCode = errorCode;
    }

    public BusinessException(ErrorCode errorCode, List<FieldError> fieldErrors) {
        super(errorCode.name());
        this.detail = "Validation failed";
        this.errorCode = errorCode;
        this.fieldErrors.addAll(fieldErrors);
    }

    public BusinessException(ErrorCode errorCode, String detail) {
        super(errorCode.name() + ": " + detail);
        this.detail = detail;
        this.errorCode = errorCode;
    }

    public BusinessException(ErrorCode errorCode, String field, String detail) {
        super(errorCode.name() + ": " + detail);
        this.detail = field + ": " + detail;
        this.errorCode = errorCode;
        this.fieldErrors.add(new FieldError(errorCode, field, detail));
    }



}