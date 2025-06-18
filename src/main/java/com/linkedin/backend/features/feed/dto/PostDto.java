package com.linkedin.backend.features.feed.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.linkedin.backend.features.authentication.model.User;
import com.linkedin.backend.features.feed.model.*;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public class PostDto {
    Long id;
    String content;
    User author;
    LocalDateTime creationDate;
    LocalDateTime updateDate;
    List<PostMedia> postMedia;
    Integer reactCount;
    Integer commentCount;
    PostBackground postBg;
    POST_TYPE postType;
}
