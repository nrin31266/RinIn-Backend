package com.linkedin.backend.features.feed.repository;

import com.linkedin.backend.features.feed.model.PostBackground;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostBackgroundRepository extends JpaRepository<PostBackground, Long> {
}
