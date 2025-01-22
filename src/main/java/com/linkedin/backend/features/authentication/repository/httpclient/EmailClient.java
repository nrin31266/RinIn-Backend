package com.linkedin.backend.features.authentication.repository.httpclient;

import com.linkedin.backend.features.authentication.dto.request.SendEmail;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;


@FeignClient(url = "https://api.brevo.com", name = "brevo")
public interface EmailClient {
    @PostMapping(value = "/v3/smtp/email", produces = MediaType.APPLICATION_JSON_VALUE)
    Object sendEmail(
            @RequestHeader("api-key") String apiKey,
            @RequestBody SendEmail request
    );
}
