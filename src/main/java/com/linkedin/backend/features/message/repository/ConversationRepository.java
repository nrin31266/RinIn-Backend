package com.linkedin.backend.features.message.repository;

import com.linkedin.backend.features.authentication.model.User;
import com.linkedin.backend.features.message.dto.ConversationDetailsDto;
import com.linkedin.backend.features.message.dto.ConversationDto;
import com.linkedin.backend.features.message.model.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {

    @Query("SELECT c FROM Conversation c LEFT JOIN FETCH c.messages WHERE c.id = :id")
    Optional<Conversation> customFindByIdWithMessages(@Param("id") Long id);


    /////////////////////////////////////


    @Query("""
                SELECT new com.linkedin.backend.features.message.dto.ConversationDto(
                    c.id,
                    c.isGroup,
                    CASE WHEN c.isGroup = true THEN c.name ELSE null END,
                    CASE WHEN c.isGroup = false THEN (
                        SELECT p.user.id FROM ConversationParticipant p
                        WHERE p.conversation.id = c.id AND p.user.id <> :userId
                    ) ELSE null END,
            
                    CASE WHEN c.isGroup = false THEN (
                        SELECT p.user.lastName FROM ConversationParticipant p
                        WHERE p.conversation.id = c.id AND p.user.id <> :userId
                    ) ELSE null END,
                    CASE WHEN c.isGroup = false THEN (
                        SELECT p.user.profilePicture FROM ConversationParticipant p
                        WHERE p.conversation.id = c.id AND p.user.id <> :userId
                    ) ELSE null END,
                    m.id,
                    m.content,
                    m.createdAt,
            
                    cp.unreadCount
                )
                FROM Conversation c
                JOIN c.participants cp
                LEFT JOIN c.lastMessage m
                WHERE cp.user.id = :userId
                ORDER BY m.createdAt DESC
            """)
    List<ConversationDto> findAllConversations(@Param("userId") Long userId);

    //    Long conversationId;
//    Boolean isGroup;
//    String groupName;
//    Long totalMembers;
//    Long otherUserId;
//    String otherUserFirstName;
//    String otherUserLastName;
//    String otherUserProfilePictureUrl;
    @Query("""
                SELECT new com.linkedin.backend.features.message.dto.ConversationDetailsDto(
                    c.id,
                    c.isGroup,
                    CASE WHEN c.isGroup = true THEN c.name ELSE null END,
                    COUNT(p.user.id),
                    CASE WHEN c.isGroup = false THEN (
                        SELECT p.user.id FROM ConversationParticipant p
                        WHERE p.conversation.id = c.id AND p.user.id <> :userId
                    ) ELSE null END,
                    CASE WHEN c.isGroup = false THEN (
                        SELECT p.user.firstName FROM ConversationParticipant p
                        WHERE p.conversation.id = c.id AND p.user.id <> :userId
                    ) ELSE null END,
                    CASE WHEN c.isGroup = false THEN (
                        SELECT p.user.lastName FROM ConversationParticipant p
                        WHERE p.conversation.id = c.id AND p.user.id <> :userId
                    ) ELSE null END,
                    CASE WHEN c.isGroup = false THEN (
                        SELECT p.user.profilePicture FROM ConversationParticipant p
                        WHERE p.conversation.id = c.id AND p.user.id <> :userId
                    ) ELSE null END,
                    CASE WHEN c.isGroup = false THEN (
                        SELECT p.user.position FROM ConversationParticipant p
                        WHERE p.conversation.id = c.id AND p.user.id <> :userId
                    ) ELSE null END,
                    CASE WHEN c.isGroup = false THEN (
                        SELECT p.user.company FROM ConversationParticipant p
                        WHERE p.conversation.id = c.id AND p.user.id <> :userId
                    ) ELSE null END
                )
                FROM Conversation c
                JOIN c.participants p
                WHERE c.id = :conversationId
                GROUP BY c.id, c.isGroup, c.name
            """)
    Optional<ConversationDetailsDto> findConversationById(@Param("conversationId") Long conversationId, @Param("userId") Long userId);


    @Query("""
                SELECT c FROM Conversation c
                WHERE c.isGroup = false
                AND SIZE(c.participants) = 2
                AND EXISTS (
                    SELECT p1 FROM ConversationParticipant p1
                    WHERE p1.conversation = c AND p1.user = :user1
                )
                AND EXISTS (
                    SELECT p2 FROM ConversationParticipant p2
                    WHERE p2.conversation = c AND p2.user = :user2
                )
            """)
    Optional<Conversation> findOneToOneConversationBetweenUsers(User user1, User user2);


}
