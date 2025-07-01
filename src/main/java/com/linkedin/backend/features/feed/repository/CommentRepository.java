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

    @Query("""
       SELECT c.parentComment.id, COUNT(c)
       FROM Comment c
       WHERE c.parentComment.id IN :parentCommentIds
       GROUP BY c.parentComment.id
       """)
    List<Object[]> countRepliesByCommentIds(List<Long> parentCommentIds);



    @Query("""
        SELECT c 
        FROM Comment c
        WHERE c.post.id = :postId AND c.type = 'POST'
        ORDER BY c.creationDate DESC
    """)
    List<Comment> findByPostIdAndTypeIsPost(Long postId);
    @Query("""
        SELECT c 
        FROM Comment c
        WHERE c.postMedia.id = :postMediaId AND c.type = 'POST_MEDIA'
        ORDER BY c.creationDate DESC
    """)
    List<Comment> findByPostMediaIdAndTypeIsPostMedia(Long postMediaId);
    @Query("""
        SELECT c 
        FROM Comment c
        WHERE c.parentComment.id = :parentCommentId AND c.type = 'REPLY'
        ORDER BY c.creationDate DESC
    """)
    List<Comment> findByParentCommentIdAndTypeIsReply(Long parentCommentId);
}
