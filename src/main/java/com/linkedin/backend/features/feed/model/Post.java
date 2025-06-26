package com.linkedin.backend.features.feed.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.linkedin.backend.features.authentication.model.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Lob
    @Column(columnDefinition = "MEDIUMTEXT")
    String content;
    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    User author;
    @CreationTimestamp
    LocalDateTime creationDate;
    LocalDateTime updateDate;
    @OneToMany(mappedBy = "post", orphanRemoval = true, cascade = CascadeType.ALL)
    List<PostMedia> postMedias;
    @JsonIgnore
    @OneToMany(mappedBy = "post", orphanRemoval = true)
    Set<React> reacts;
    @JsonIgnore
    @OneToMany(mappedBy = "post", orphanRemoval = true)
    Set<Comment> comments;
    @ManyToOne
    @JoinColumn(name = "background_id")
    PostBackground postBg;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    POST_TYPE postType;
    @PreUpdate
    public void preUpdate() {
        this.updateDate = LocalDateTime.now();
    }
}
