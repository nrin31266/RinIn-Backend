package com.linkedin.backend.features.feed.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentRequest {
    @NotBlank
    TARGET_ACTION targetAction;
    @NotBlank
    String content;
    @NotBlank
    Long targetId; // ID of the post or comment being replied to

    Long repliedToId; // ID of the user being replied to, if applicable
}
