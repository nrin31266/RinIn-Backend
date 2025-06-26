package com.linkedin.backend.features.feed.repository;

import com.linkedin.backend.features.feed.model.React;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReactRepository extends JpaRepository<React, Long> {
    // Custom query methods can be defined here if needed
    // For example, to find all reactions by a user for a specific post:
    // List<React> findByPostIdAndUserId(Long postId, Long userId);
    Optional<React> findByPostIdAndAuthorId(Long postId, Long authorId);
    Optional<React> findByPostMediaIdAndAuthorId(Long postMediaId, Long authorId);
    Optional<React> findByCommentIdAndAuthorId(Long commentId, Long authorId);
}
