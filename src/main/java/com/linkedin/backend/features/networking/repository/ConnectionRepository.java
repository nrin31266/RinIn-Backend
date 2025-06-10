package com.linkedin.backend.features.networking.repository;

import com.linkedin.backend.features.authentication.model.User;
import com.linkedin.backend.features.networking.domain.CONNECTION_STATUS;
import com.linkedin.backend.features.networking.model.Connection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ConnectionRepository extends JpaRepository<Connection, Long> {

    Boolean existsByAuthorAndRecipient(User author, User recipient);
    Boolean existsByRecipientAndAuthor(User author, User recipient);



    @Query("SELECT c FROM Connection c WHERE (c.author.id =:userId OR c.recipient.id =: userId) AND c.status =:status")
    List<Connection> cFindConnectionsByUserAndStatus(@Param("userId") Long userId,
                                                     @Param("status") CONNECTION_STATUS status);

    List<Connection> findConnectionByAuthorAndStatusOrRecipientAndStatus(
            User user, CONNECTION_STATUS status,
            User user1, CONNECTION_STATUS status1
    );

    List<Connection> findAllByRecipientOrAuthor(User u1, User u2);
}
