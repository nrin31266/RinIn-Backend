package com.linkedin.backend.features.authentication.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SendEmail {
    Sender sender;
    List<Receive> to;
    String subject;
    String htmlContent;
}
