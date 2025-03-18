package com.linkedin.backend.features.message.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.linkedin.backend.features.authentication.model.User;
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
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    User sender;

    @ManyToOne
    User receiver;

    @JsonIgnore
    @ManyToOne(optional = false)
    Conversation conversation;

    String content;

    Boolean isRead = false;

    @CreationTimestamp
    LocalDateTime createdAt;


}
