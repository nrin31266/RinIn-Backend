package com.linkedin.backend.features.authentication.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ExchangeCodeForTokenRequest {
    @JsonProperty("code")
    String code;
    @JsonProperty("redirect_uri")
    String redirect_uri;
    @JsonProperty("client_id")
    String client_id;
    @JsonProperty("client_secret")
    String client_secret;
    @JsonProperty("grant_type")
    String grant_type = "authorization_code";
}
