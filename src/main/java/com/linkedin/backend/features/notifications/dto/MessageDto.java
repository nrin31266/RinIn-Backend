package com.linkedin.backend.features.notifications.dto;

import com.linkedin.backend.features.notifications.domain.MessageHandleType;
import jakarta.persistence.Entity;
import lombok.*;
import lombok.experimental.FieldDefaults;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MessageDto {
    MessageHandleType type;
}
