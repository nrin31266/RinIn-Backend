package com.linkedin.backend.features.feed.dto;

import com.linkedin.backend.features.authentication.model.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentDto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    User author;
    String content;
    LocalDateTime creationDate;
    LocalDateTime updateDate;
//    // Comment cha (nếu có)
//    @JsonIgnore
//    @ManyToOne
//    @JoinColumn(name = "parent_comment_id")
//    Comment parentComment;
    Long parentCommentId; // ID of the parent comment, if applicable
    User repliedTo; // User being replied to, if applicable
    Integer repliedCount; // Count of replies to this comment
    Long postId; // ID of the post this comment belongs to
}
