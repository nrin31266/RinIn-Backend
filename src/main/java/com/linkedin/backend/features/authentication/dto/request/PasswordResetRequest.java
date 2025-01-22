package com.linkedin.backend.features.authentication.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PasswordResetRequest {
    String token;
    String newPassword;
    String email;
}
