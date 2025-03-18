package com.linkedin.backend.features.message.repository;

import com.linkedin.backend.features.message.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long> {

}
