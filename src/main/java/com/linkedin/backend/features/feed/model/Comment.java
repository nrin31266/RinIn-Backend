package com.linkedin.backend.features.feed.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.linkedin.backend.features.authentication.model.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    Long id;

    @JoinColumn(name = "post_id", nullable = false)
    @JsonIgnore
    @ManyToOne
    Post post;

    @JoinColumn(name = "post_media_id")
    @JsonIgnore
    @ManyToOne
    PostMedia postMedia;

    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    User author;

    @Column(nullable = false, length = 999)
    String content;

    @JsonIgnore
    @OneToMany(mappedBy = "comment", orphanRemoval = true, cascade = CascadeType.ALL)
    Set<React> reacts;

    LocalDateTime creationDate;
    LocalDateTime updateDate;

    // Comment cha (nếu có)
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "parent_comment_id")
    Comment parentComment;

    @JsonIgnore
    @OneToMany(mappedBy = "parentComment", cascade = CascadeType.ALL, orphanRemoval = true)
    Set<Comment> replies;


    @JoinColumn(name = "replied_to_id")
    @ManyToOne
    User repliedTo; // User being replied to, if applicable

    @Enumerated(EnumType.STRING)
    COMMENT_TYPE type;

    @PrePersist
    protected void onCreate() {
        this.creationDate = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updateDate = LocalDateTime.now();
    }
}
