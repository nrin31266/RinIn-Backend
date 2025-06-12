package com.linkedin.backend.features.authentication.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.linkedin.backend.features.feed.model.Post;
import com.linkedin.backend.features.message.model.Conversation;
import com.linkedin.backend.features.networking.model.Connection;
import com.linkedin.backend.features.notifications.model.Notification;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(unique = true, nullable = false)
    String email;
    @Column(nullable = false)
    Boolean emailVerified;
    @JsonIgnore
    String emailVerificationToken;
    @JsonIgnore
    Date emailVerificationTokenExpiryDate;
    @JsonIgnore
    String password;
    @JsonIgnore
    private String passwordResetToken;
    @JsonIgnore
    private Date passwordResetTokenExpiryDate;

    String firstName;
    String lastName;
    String company;
    String position;
    String location;

    Boolean profileComplete;
    String profilePicture;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "author")
    List<Post> posts;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "recipient")
    List<Notification> receivedNotifications;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "actor")
    List<Notification> actedNotifications;

    @JsonIgnore
    @OneToMany(mappedBy = "createdBy", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Conversation> conversationsAsOwner;



    @JsonIgnore
    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Connection> initiatedConnections;



    @PrePersist
    public void prePersist() {
        emailVerified = false;
        profileComplete = false;
    }
}
