package com.linkedin.backend.features.notifications.model;

import com.linkedin.backend.features.authentication.model.User;
import com.linkedin.backend.features.notifications.domain.NotificationType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @ManyToOne
    User recipient;
    @ManyToOne
    User actor;
    Boolean isRead;
    @Enumerated(EnumType.STRING)
    NotificationType type;
    Long resourceId;

    @CreationTimestamp
    LocalDateTime creationDate;

}
