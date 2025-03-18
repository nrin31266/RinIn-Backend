package com.linkedin.backend.features.message.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.linkedin.backend.features.authentication.model.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

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
    User author;
    @ManyToOne(optional = false)
    User recipient;
    @OneToMany(mappedBy = "conversation", orphanRemoval = true, fetch = FetchType.EAGER)
    List<Message> messages = new ArrayList<>();

}
