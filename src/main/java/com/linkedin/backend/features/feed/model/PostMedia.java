package com.linkedin.backend.features.feed.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class PostMedia {
    @jakarta.persistence.Id
    @jakarta.persistence.GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    Long id;
    String content;

    String mediaUrl;
    @Enumerated(jakarta.persistence.EnumType.STRING)
    MEDIA_TYPE mediaType;

    int height;
    int width;
    Integer duration; // in seconds, only for videos

    @JoinColumn(name = "post_id", nullable = false)
    @ManyToOne
    Post post;
}
enum MEDIA_TYPE {
    IMAGE,
    VIDEO,
}
