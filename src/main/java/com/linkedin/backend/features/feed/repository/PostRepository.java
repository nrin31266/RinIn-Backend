package com.linkedin.backend.features.feed.repository;


import com.linkedin.backend.features.feed.model.Post;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByAuthorId(Long authorId);

    List<Post> findAllByOrderByCreationDateDesc();


//    @Query("SELECT p FROM posts p LEFT JOIN FETCH p.comments WHERE p.id = :postId")
//    Optional<Post> findByIdWithComments(@Param("postId") Long postId);

//    @Query("SELECT p FROM posts p LEFT JOIN FETCH p.likes WHERE p.id = :postId")
//    Optional<Post> findByIdWithLikes(@Param("postId") Long postId);


    @Query("""
            SELECT r.post.id, r.reactType, COUNT(*) FROM React r 
            WHERE r.post.id IN :postIds
            GROUP BY r.post.id, r.reactType
            """)
    List<Object[]> countReactionsByTypeForPosts(@Param("postIds") List<Long> postIds);

    List<Post> findAllByAuthorIdOrderByCreationDate(Long authorId);
}
