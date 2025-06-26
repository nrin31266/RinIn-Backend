package com.linkedin.backend.features.feed.repository;

import com.linkedin.backend.features.feed.model.PostMedia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostMediaRepository extends JpaRepository<PostMedia, Long> {
    // Custom query methods can be defined here if needed
    // For example, to find all media by a specific post:
    // List<PostMedia> findByPostId(Long postId);

}
