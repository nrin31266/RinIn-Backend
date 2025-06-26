package com.linkedin.backend.features.feed.dto.request;

import com.linkedin.backend.features.feed.model.REACT_TYPE;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReactRequest {
    @NotBlank
    Long targetId;
    @NotBlank
    REACT_TYPE reactType;
    @NotBlank
    TARGET_ACTION targetAction;
}
