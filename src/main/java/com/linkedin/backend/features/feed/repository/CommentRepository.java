package com.linkedin.backend.features.feed.repository;

import com.linkedin.backend.features.feed.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("""
       SELECT c.post.id, COUNT(c) 
       FROM Comment c 
       WHERE c.post.id 
       IN :postIds 
       GROUP BY c.post.id
       """)
    List<Object[]> countCommentsByPostIds(List<Long> postIds);
}
