package com.linkedin.backend.features.authentication.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.GenericField;
import com.linkedin.backend.features.feed.model.Post;
import com.linkedin.backend.features.follow.model.Follow;
import com.linkedin.backend.features.message.model.Conversation;
import com.linkedin.backend.features.networking.model.Connection;
import com.linkedin.backend.features.notifications.model.Notification;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity(name = "users")
@Indexed(index = "users") // Cho phép Hibernate Search lập chỉ mục entity này
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(unique = true, nullable = false)
    @FullTextField
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
    @FullTextField
    String firstName;
    @FullTextField
    String lastName;
    @FullTextField
    String company;
    @FullTextField
    String position;
    @FullTextField
    String location;
    @FullTextField
    String about;
    @GenericField
    Boolean profileComplete;
    String profilePicture;
    LocalDateTime lastLogin;
    @CreationTimestamp
    @GenericField
    LocalDateTime creationDate;


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

    @JsonIgnore
    @OneToMany(mappedBy = "follower", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Follow> followers;

    @JsonIgnore
    @OneToMany(mappedBy = "following", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Follow> following;


    @PrePersist
    public void prePersist() {
        profileComplete = false;
    }
}
