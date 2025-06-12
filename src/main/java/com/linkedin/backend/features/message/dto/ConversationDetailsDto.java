package com.linkedin.backend.features.message.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ConversationDetailsDto {
    Long conversationId;
    Boolean isGroup;
    String groupName;
    Long totalMembers;
    Long otherUserId;
    String otherUserFirstName;
    String otherUserLastName;
    String otherUserProfilePictureUrl;

    public ConversationDetailsDto(
            Long conversationId,
            Boolean isGroup,
            String groupName,
            Long totalMembers,
            Long otherUserId,
            String otherUserFirstName,
            String otherUserLastName,
            String otherUserProfilePictureUrl
    ) {
        this.conversationId = conversationId;
        this.isGroup = isGroup;
        this.groupName = groupName;
        this.totalMembers = totalMembers;
        this.otherUserId = otherUserId;
        this.otherUserFirstName = otherUserFirstName;
        this.otherUserLastName = otherUserLastName;
        this.otherUserProfilePictureUrl = otherUserProfilePictureUrl;
    }
}
