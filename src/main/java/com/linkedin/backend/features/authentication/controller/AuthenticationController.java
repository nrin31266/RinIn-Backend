package com.linkedin.backend.features.authentication.controller;


import com.linkedin.backend.dto.ApiResponse;
import com.linkedin.backend.features.authentication.dto.request.AuthenticationUserRequestBody;
import com.linkedin.backend.features.authentication.dto.request.PasswordResetRequest;
import com.linkedin.backend.features.authentication.dto.request.SendEmailRequest;
import com.linkedin.backend.features.authentication.dto.request.UpdateUserRequest;
import com.linkedin.backend.features.authentication.dto.response.AuthenticationUserResponseBody;
import com.linkedin.backend.features.authentication.model.User;
import com.linkedin.backend.features.authentication.service.AuthenticationUserService;
import com.linkedin.backend.utils.EmailService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/authentication")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {
    AuthenticationUserService authenticationUserService;
    EmailService emailService;


    @GetMapping("/user")
    public ApiResponse<Object> getUser(@RequestAttribute("authenticatedUser") User user) {
        return ApiResponse.builder()
                .data(authenticationUserService.getUser(user.getEmail()))
                .build();
    }

    @PostMapping("/register")
    public ApiResponse<AuthenticationUserResponseBody> register(@RequestBody @Validated AuthenticationUserRequestBody authenticationUserRequestBody) {
        return ApiResponse.<AuthenticationUserResponseBody>builder()
                .data(authenticationUserService.register(authenticationUserRequestBody))
                .build();
    }

    @PostMapping("/login")
    public ApiResponse<AuthenticationUserResponseBody> login(@RequestBody @Validated AuthenticationUserRequestBody authenticationUserRequestBody) {
        return ApiResponse.<AuthenticationUserResponseBody>builder()
                .data(authenticationUserService.login(authenticationUserRequestBody))
                .build();
    }

    @PostMapping("/send-email")
    public ApiResponse<?> sendEmail(@RequestBody @Validated SendEmailRequest sendEmailRequest) {
        return ApiResponse.builder()
                .data(emailService.sendEmail(sendEmailRequest))
                .message("Send email successful")
                .build();
    }

    @PutMapping("/validate-email-verification-token")
    public ApiResponse verifyEmail(@RequestParam String token, @RequestAttribute("authenticatedUser") User user) {
        authenticationUserService.validateEmailVerificationToken(token, user.getEmail());
        return ApiResponse.builder()
                .message("Validate email successful")
                .build();
    }

    @PutMapping("/send-email-verification-token")
    public ApiResponse sendEmailVerificationToken(@RequestAttribute("authenticatedUser") User user) {
        authenticationUserService.sendEmailVerifyToken(user.getEmail());
        return ApiResponse.builder()
                .message("Send email verification code successful")
                .build();
    }


    @PutMapping("/send-password-reset-token")
    public ApiResponse sendPasswordResetToken(@RequestParam String email) {
        authenticationUserService.sendPasswordResetToken(email);
        return ApiResponse.builder()
                .message("Send password reset token successful")
                .build();
    }

    @PutMapping("/reset-password")
    public ApiResponse resetPassword(@RequestBody @Validated PasswordResetRequest request) {
        authenticationUserService.resetPassword(request);
        return ApiResponse.builder()
                .message("Password reset token successful")
                .build();
    }

    @PutMapping("/profile/{id}")
    public ApiResponse<User> updateProfile(@PathVariable("id") Long id, @RequestBody @Validated UpdateUserRequest updateUserRequest, @RequestAttribute("authenticatedUser") User authenticatedUser) {
        return ApiResponse.<User>builder()
                .data(authenticationUserService.updateUserProfile(id, updateUserRequest, authenticatedUser))
                .build();
    }

    @DeleteMapping("/user/delete")
    public ApiResponse deleteUser(@RequestAttribute("authenticatedUser") User user) {
        authenticationUserService.deleteUser(user.getId());
        return ApiResponse.builder()
                .message("Delete user successful")
                .build();
    }

    @GetMapping("/users")
    public ApiResponse<List<User>> getUsersWithoutAuthenticated(@RequestAttribute("authenticatedUser") User user) {

        return ApiResponse.<List<User>>builder()
                .data(authenticationUserService.getUserWithoutAuthenticated(user))
                .build();
    }

}
