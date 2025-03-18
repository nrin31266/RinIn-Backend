package com.linkedin.backend.features.message.repository;

import com.linkedin.backend.features.authentication.model.User;
import com.linkedin.backend.features.message.model.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {
    Optional<Conversation> findByAuthorAndRecipient(User author, User recipient);
    List<Conversation> findByAuthorOrRecipient(User userOne, User userTwo);

    @Query("SELECT c FROM Conversation c LEFT JOIN FETCH c.messages WHERE c.id = :id")
    Optional<Conversation> customFindByIdWithMessages(@Param("id") Long id);

}
