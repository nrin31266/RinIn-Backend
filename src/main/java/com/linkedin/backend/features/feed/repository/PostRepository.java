package com.linkedin.backend.features.feed.repository;


import com.linkedin.backend.features.feed.model.Post;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByAuthorId(Long authorId);

    List<Post> findAllByOrderByCreationDateDesc();

    List<Post> findByAuthorIdOrderByCreationDateDesc(Long authenticatedUserId);
}
