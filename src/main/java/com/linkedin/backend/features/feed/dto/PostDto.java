package com.linkedin.backend.features.feed.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.linkedin.backend.features.authentication.model.User;
import com.linkedin.backend.features.feed.model.*;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PostDto {
    Long id;
    String content;
    User author;
    LocalDateTime creationDate;
    LocalDateTime updateDate;
    List<PostMedia> postMedias;
    Map<REACT_TYPE, Integer> reactCounts;
    Boolean isReacted;
    REACT_TYPE myReactType;
    Integer commentCount;
    PostBackground postBg;
    POST_TYPE postType;
}
