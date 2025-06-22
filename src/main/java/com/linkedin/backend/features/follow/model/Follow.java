package com.linkedin.backend.features.follow.model;

import com.linkedin.backend.features.authentication.model.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class Follow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @ManyToOne
    @JoinColumn(name = "follower_id", nullable = false)
    User follower; // User who is following
    @ManyToOne
    @JoinColumn(name = "following_id", nullable = false)
    User following; // User who is being followed

    @CreationTimestamp
    LocalDateTime creationDate;
}
