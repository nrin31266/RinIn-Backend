package com.linkedin.backend.features.message.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.linkedin.backend.features.authentication.model.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Conversation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @ManyToOne(optional = false)
    User createdBy;
    String name; // Tên nhóm nếu là group
    Boolean isGroup = false;
    @JsonIgnore
    @OneToMany(mappedBy = "conversation", orphanRemoval = true, cascade = CascadeType.ALL)
    List<Message> messages = new ArrayList<>();
    @JsonIgnore
    @OneToMany(mappedBy = "conversation", orphanRemoval = true, cascade = CascadeType.ALL)
    List<ConversationParticipant> participants = new ArrayList<>();

    @JoinColumn(name = "last_message_id")
    @OneToOne(cascade = CascadeType.ALL)
    Message lastMessage;

    @CreationTimestamp
    LocalDateTime createdAt = LocalDateTime.now();
}
