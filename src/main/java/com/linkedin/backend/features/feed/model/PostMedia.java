package com.linkedin.backend.features.feed.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

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
    String thumbnailUrl; // only for videos

    @JsonIgnore
    @JoinColumn(name = "post_id", nullable = false)
    @ManyToOne
    Post post;

    @JsonIgnore
    @OneToMany(mappedBy = "postMedia", cascade = CascadeType.ALL, orphanRemoval = true)
    Set<React> reacts;

    @JsonIgnore
    @OneToMany(mappedBy = "postMedia", cascade = CascadeType.ALL, orphanRemoval = true)
    Set<Comment> comments;

}
