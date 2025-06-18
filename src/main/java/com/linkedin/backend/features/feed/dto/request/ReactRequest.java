package com.linkedin.backend.features.feed.dto.request;

import com.linkedin.backend.features.feed.model.REACT_TYPE;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReactRequest {
    Long postId;
    REACT_TYPE reactType;
}
