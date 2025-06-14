package com.linkedin.backend.features.message.dto;

import com.linkedin.backend.features.message.model.ConversationParticipant;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

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
    String otherUserPosition;
    String otherUserCompany;
    LocalDateTime myLastReadAt;
    List<ConversationParticipant> participants;

    public ConversationDetailsDto(
            Long conversationId,
            Boolean isGroup,
            String groupName,
            Long totalMembers,
            Long otherUserId,
            String otherUserFirstName,
            String otherUserLastName,
            String otherUserProfilePictureUrl,
            String otherUserPosition,
            String otherUserCompany,
            LocalDateTime myLastReadAt
    ) {
        this.conversationId = conversationId;
        this.isGroup = isGroup;
        this.groupName = groupName;
        this.totalMembers = totalMembers;
        this.otherUserId = otherUserId;
        this.otherUserFirstName = otherUserFirstName;
        this.otherUserLastName = otherUserLastName;
        this.otherUserProfilePictureUrl = otherUserProfilePictureUrl;
        this.otherUserPosition = otherUserPosition;
        this.otherUserCompany = otherUserCompany;
        this.myLastReadAt = myLastReadAt;
    }
}
