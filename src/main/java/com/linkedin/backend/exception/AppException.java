package com.linkedin.backend.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
    public class AppException extends RuntimeException {
    public AppException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public AppException(ErrorCode errorCode, String customMessage) {
        super(customMessage);
        this.errorCode = errorCode;
    }

    public AppException(String message) {
        super(message);
        this.errorCode = ErrorCode.DEFAULT_ERROR;
    }

    final ErrorCode errorCode;
}
