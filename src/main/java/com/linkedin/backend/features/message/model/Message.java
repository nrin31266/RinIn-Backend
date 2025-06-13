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

    @JsonIgnore
    @ManyToOne(optional = false, cascade = CascadeType.ALL)
    Conversation conversation;

    @Column(length = 65535, columnDefinition = "TEXT")
    String content;

//    Boolean isRead = false;

    LocalDateTime createdAt;


}
