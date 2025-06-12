package com.linkedin.backend.features.message.repository;

import com.linkedin.backend.features.message.model.ConversationParticipant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ConversationParticipantRepository extends JpaRepository<ConversationParticipant, Long> {
    // Define any additional query methods if needed
    Optional<ConversationParticipant> findByUserIdAndConversationId(Long userId, Long conversationId);
    Boolean existsByUserIdAndConversationId(Long userId, Long conversationId);
}
