package com.linkedin.backend.features.authentication.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.linkedin.backend.features.feed.model.Post;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;
import java.util.List;

@Data
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
    String emailVerificationToken;
    Date emailVerificationTokenExpiryDate;
    @JsonIgnore
    String password;
    private String passwordResetToken;
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

    @PrePersist
    public void prePersist() {
        emailVerified = false;
        profileComplete = false;
    }
}
