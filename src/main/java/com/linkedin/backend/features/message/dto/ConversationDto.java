package com.linkedin.backend.features.message.dto;

import com.linkedin.backend.features.authentication.model.User;
import com.linkedin.backend.features.message.model.Message;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ConversationDto {
    Long conversationId;
    Boolean isGroup;
    String groupName;
    Long otherUserId;
    String otherUserName;
    String otherUserProfilePictureUrl;
    Long lastMessageId;
    String lastMessageContent;
    LocalDateTime lastMessageCreatedAt;
    Integer unreadCount;

    public ConversationDto(
            Long conversationId,
            Boolean isGroup,
            String groupName,
            Long otherUserId,
            String otherUserName,
            String otherUserProfilePictureUrl,
            Long lastMessageId,
            String lastMessageContent,
            LocalDateTime lastMessageCreatedAt,
            Integer unreadCount
    ) {
        this.conversationId = conversationId;
        this.isGroup = isGroup;
        this.groupName = groupName;
        this.otherUserId = otherUserId;
        this.otherUserName = otherUserName;
        this.otherUserProfilePictureUrl = otherUserProfilePictureUrl;
        this.lastMessageId = lastMessageId;
        this.lastMessageContent = lastMessageContent;
        this.lastMessageCreatedAt = lastMessageCreatedAt;
        this.unreadCount = unreadCount;
    }

}


