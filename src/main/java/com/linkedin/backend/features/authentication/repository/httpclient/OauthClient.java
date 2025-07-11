package com.linkedin.backend.features.authentication.repository.httpclient;

import com.linkedin.backend.features.authentication.dto.request.ExchangeCodeForTokenRequest;
import com.linkedin.backend.features.authentication.dto.response.ExchangeCodeForTokenResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "oauth", url = "https://oauth2.googleapis.com")
public interface OauthClient {
    @PostMapping(value = "/token", consumes = "application/x-www-form-urlencoded", produces = "application/json")
    ExchangeCodeForTokenResponse exchangeCodeForToken(@RequestBody ExchangeCodeForTokenRequest request);

}
