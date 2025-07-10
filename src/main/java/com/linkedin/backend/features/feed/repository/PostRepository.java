package com.linkedin.backend.features.feed.repository;


import com.linkedin.backend.features.feed.model.Post;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByAuthorId(Long authorId);

    List<Post> findAllByOrderByCreationDateDesc();


//    @Query("SELECT p FROM posts p LEFT JOIN FETCH p.comments WHERE p.id = :postId")
//    Optional<Post> findByIdWithComments(@Param("postId") Long postId);

//    @Query("SELECT p FROM posts p LEFT JOIN FETCH p.likes WHERE p.id = :postId")
//    Optional<Post> findByIdWithLikes(@Param("postId") Long postId);




    List<Post> findAllByAuthorIdOrderByCreationDate(Long authorId);

    @Query("""
            SELECT p FROM Post p
            WHERE p.author.id IN (
                SELECT f.following.id
                FROM Follow f
                WHERE f.follower.id = :userId
            ) OR p.author.id = :userId
            ORDER BY p.creationDate DESC
            """)
    List<Post> findPosts(@Param("userId") Long userId);

    @Query("""
    SELECT p
    FROM Post p
    WHERE p.author.id = :userId
    ORDER BY p.creationDate DESC
    """)
    List<Post> findPostsByUserId(@Param("userId") Long userId);
}
