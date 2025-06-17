package com.linkedin.backend.features.feed.dto.request;

import com.linkedin.backend.features.authentication.model.User;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PostRequest {
    @NotBlank(message = "POST_CONTENT_IS_REQUIRED")
    String content;
    String picture;
    Long postBgId;
}
