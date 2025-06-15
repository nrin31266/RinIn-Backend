package com.linkedin.backend.features.message.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.linkedin.backend.features.authentication.model.User;
import com.linkedin.backend.features.message.model.Conversation;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ParticipantDto {
    Long id;
    Long conversationId;
    User user;
    Integer unreadCount = 0;
    LocalDateTime lastReadAt;

    Boolean conversationIsGroup;
    Long otherParticipantId;
    LocalDateTime lastReadAtForOtherParticipant;
}
