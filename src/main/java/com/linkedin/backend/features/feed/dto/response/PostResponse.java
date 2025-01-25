package com.linkedin.backend.features.feed.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.linkedin.backend.features.authentication.model.User;
import com.linkedin.backend.features.feed.model.Comment;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PostResponse {
    Long id;
    String content;
    String picture;
    User author;
    LocalDateTime creationDate;
    LocalDateTime updateDate;
    Set<User> likes;
    List<Comment> comments;
}
