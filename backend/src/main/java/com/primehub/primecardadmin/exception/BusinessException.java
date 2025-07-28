package com.primehub.primecardadmin.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BusinessException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final int errorCode;
    private final int httpStatus;

    public BusinessException(String message) {
        super(message);
        this.errorCode = HttpStatus.BAD_REQUEST.value();
        this.httpStatus = HttpStatus.BAD_REQUEST.value();
    }

    public BusinessException(String message, int errorCode) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = HttpStatus.BAD_REQUEST.value();
    }

    public BusinessException(String message, int errorCode, int httpStatus) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }

    public BusinessException(String message, HttpStatus httpStatus) {
        super(message);
        this.errorCode = httpStatus.value();
        this.httpStatus = httpStatus.value();
    }
}