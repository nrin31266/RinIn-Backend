package com.linkedin.backend.features.networking.model;

import com.linkedin.backend.features.authentication.model.User;
import com.linkedin.backend.features.networking.domain.CONNECTION_STATUS;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@Data
public class Connection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    User author;

    @ManyToOne
    @JoinColumn(name = "recipient_id", nullable = false)
    User recipient;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    CONNECTION_STATUS status = CONNECTION_STATUS.PENDING;

    Boolean seen = false;

    @CreationTimestamp
    LocalDateTime connectionDate;
}
