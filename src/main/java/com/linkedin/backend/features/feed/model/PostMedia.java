package com.linkedin.backend.features.feed.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
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
    @Lob
    @Column(columnDefinition = "MEDIUMTEXT")
    String content;

    String mediaUrl;
    @Enumerated(jakarta.persistence.EnumType.STRING)
    MEDIA_TYPE mediaType;

    int height;
    int width;
    Integer duration; // in seconds, only for videos

    @JsonIgnore
    @JoinColumn(name = "post_id", nullable = false)
    @ManyToOne
    Post post;
}
