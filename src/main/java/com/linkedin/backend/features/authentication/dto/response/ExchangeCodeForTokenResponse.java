package com.linkedin.backend.features.authentication.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)

public class ExchangeCodeForTokenResponse {
    @JsonProperty("access_token")
    String access_token;
    @JsonProperty("expires_in")
    Long expires_in;
    @JsonProperty("scope")
     String scope;
    @JsonProperty("token_type")
    String token_type;
    @JsonProperty("id_token")
    String id_token;

}
