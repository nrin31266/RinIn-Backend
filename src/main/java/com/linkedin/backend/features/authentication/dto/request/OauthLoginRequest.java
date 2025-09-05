package com.linkedin.backend.features.authentication.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OauthLoginRequest {
    String code;
    String page;
    @NotBlank
    String deviceId;

    @NotBlank
    String deviceName;
}
