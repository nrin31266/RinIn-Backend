package com.linkedin.backend.features.feed.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentRequest {
    TARGET_ACTION targetAction;
    String content;
    Long targetId; // ID of the post or comment being replied to
}
