package com.linkedin.backend.features.message.dto;

import com.linkedin.backend.features.authentication.model.User;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CheckConversationDto {
    User receiver;
    Boolean isConversationExists;
    Long conversationId;
}
