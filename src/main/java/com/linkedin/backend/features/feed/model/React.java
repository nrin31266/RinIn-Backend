package com.linkedin.backend.features.feed.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.linkedin.backend.features.authentication.model.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class React {
    @jakarta.persistence.Id
    @jakarta.persistence.GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    Long id;

    @jakarta.persistence.ManyToOne
    User author;

    @JoinColumn(name = "post_id")
    @JsonIgnore
    @ManyToOne
    Post post;

    @JoinColumn(name = "post_media_id")
    @JsonIgnore
    @ManyToOne
    PostMedia postMedia;

    @Enumerated(jakarta.persistence.EnumType.STRING)
    REACT_TYPE reactType;

    @Column(nullable = false)
    @CreationTimestamp
    LocalDateTime creationDate;
}

enum REACT_TYPE {
    LIKE,
    LOVE,
    HAHA,
    WOW,
    SAD,
    ANGRY
}
