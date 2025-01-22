package com.linkedin.backend.utils;

import com.linkedin.backend.features.authentication.dto.request.Receive;
import com.linkedin.backend.features.authentication.dto.request.SendEmail;
import com.linkedin.backend.features.authentication.dto.request.Sender;
import com.linkedin.backend.features.authentication.dto.request.SendEmailRequest;
import com.linkedin.backend.features.authentication.repository.httpclient.EmailClient;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;


@Slf4j
@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class EmailService {
    EmailClient emailClient;

    @NonFinal
    @Value("${brevo.apiKey}")
    String apiKey;

    @NonFinal
    @Value("${brevo.email}")
    String fromEmail;

    @NonFinal
    @Value("${brevo.name}")
    String fromEmailName;

    public Object sendEmail(SendEmailRequest request) {
        return emailClient.sendEmail(
                apiKey,
                SendEmail.builder()
                        .sender(Sender.builder()
                                .email(fromEmail)
                                .name(fromEmailName)
                                .build())
                        .to(List.of(Receive.builder()
                                        .email(request.getTo())
                                .build()))
                        .subject(request.getSubject())
                        .htmlContent(request.getBody())
                        .build()
        );
    }


}
