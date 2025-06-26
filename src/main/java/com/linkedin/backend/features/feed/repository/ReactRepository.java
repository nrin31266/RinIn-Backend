package com.linkedin.backend.features.feed.repository;

import com.linkedin.backend.features.feed.model.React;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReactRepository extends JpaRepository<React, Long> {
    // Custom query methods can be defined here if needed
    // For example, to find all reactions by a user for a specific post:
    // List<React> findByPostIdAndUserId(Long postId, Long userId);
    Optional<React> findByPostIdAndAuthorId(Long postId, Long authorId);
    Optional<React> findByPostMediaIdAndAuthorId(Long postMediaId, Long authorId);
    Optional<React> findByCommentIdAndAuthorId(Long commentId, Long authorId);

    @Query("""
            SELECT r.post.id, 
            r.reactType, 
            COUNT(*),
            MAX(CASE WHEN r.author.id = :authenticatedUserId THEN true ELSE false END) AS isReacted,
            MAX(CASE WHEN r.author.id = :authenticatedUserId THEN r.reactType ELSE null END) AS myReactType\s
            FROM React r 
            WHERE r.post.id IN :postIds
            GROUP BY r.post.id, r.reactType
            """)
    List<Object[]> countReactionsByTypeForPosts(@Param("postIds") List<Long> postIds, @Param("authenticatedUserId") Long authenticatedUserId);
}
