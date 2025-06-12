package com.linkedin.backend.features.message.repository;

import com.linkedin.backend.features.message.model.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.time.LocalDateTime;


public interface MessageRepository extends JpaRepository<Message, Long> {
    @Query("""
                SELECT m FROM Message m
                WHERE m.conversation.id = :conversationId
                AND (:before IS NULL OR m.createdAt < :before)
                ORDER BY m.createdAt DESC
            """)
    Page<Message> findMessagesBeforeTime(
            @Param("conversationId") Long conversationId,
            @Param("before") LocalDateTime before,
            Pageable pageable
    );

}
