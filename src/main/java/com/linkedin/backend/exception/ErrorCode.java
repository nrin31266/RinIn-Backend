package com.linkedin.backend.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized exception", HttpStatus.INTERNAL_SERVER_ERROR),
    UNAUTHENTICATED(9001, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    TOKEN_MISSING(9002, "Token is missing", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(9003, "Unauthorized. You don't have permission", HttpStatus.UNAUTHORIZED),
    // Authentication User
    INVALID_EMAIL(1001, "Invalid email address", HttpStatus.BAD_REQUEST),
    EMAIL_ALREADY_EXISTS(1002, "Email already exists", HttpStatus.CONFLICT),
    EMAIL_IS_REQUIRED(1003, "Email is required", HttpStatus.CONFLICT),
    PASSWORD_IS_REQUIRED(1004, "Password is required", HttpStatus.CONFLICT),
    USER_NOT_FOUND(1005, "User not found", HttpStatus.NOT_FOUND),
    USER_ALREADY_EXISTS(1006, "User already exists", HttpStatus.CONFLICT),
    PASSWORD_MISMATCH(1007, "Password mismatch", HttpStatus.CONFLICT),
    TOKEN_EXPIRED(1008, "Token expired", HttpStatus.CONFLICT),
    EMAIL_ALREADY_VERIFIED(1009, "Email already verified", HttpStatus.CONFLICT),
    EMAIL_VERIFICATION_FAILED(10010, "Email verification failed", HttpStatus.CONFLICT),
    EMAIL_VERIFICATION_EXPIRED(10011, "Email verification expire", HttpStatus.CONFLICT),
    PASSWORD_RESET_TOKEN_EXPIRED(10012, "Password reset token expire", HttpStatus.CONFLICT),
    PASSWORD_RESET_FAILED(10013, "Password reset failed", HttpStatus.CONFLICT),
    NEW_PASSWORD_CAN_NOT_SAME_OLD_PASSWORD(10014, "New password cannot same old password", HttpStatus.CONFLICT),
    // User Profile
    POST_CONTENT_IS_REQUIRED(10015, "Post content is required", HttpStatus.CONFLICT),
    POST_NOT_FOUND(10016, "Post not found", HttpStatus.NOT_FOUND),
    ;


    ErrorCode(int code, String message, HttpStatusCode httpStatusCode) {
        this.code = code;
        this.message = message;
        this.statusCode=httpStatusCode;
    }


    final int code;
    final String message;
    private final HttpStatusCode statusCode;
}
