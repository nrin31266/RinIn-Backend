package com.linkedin.backend.features.authentication.repository;

import com.linkedin.backend.features.authentication.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthenticationUserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
