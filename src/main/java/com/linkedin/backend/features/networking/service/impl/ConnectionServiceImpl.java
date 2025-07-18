package com.linkedin.backend.features.networking.service.impl;

import com.linkedin.backend.exception.AppException;
import com.linkedin.backend.features.authentication.model.User;
import com.linkedin.backend.features.authentication.service.AuthenticationUserService;
import com.linkedin.backend.features.follow.model.Follow;
import com.linkedin.backend.features.follow.service.FollowService;
import com.linkedin.backend.features.networking.domain.CONNECTION_STATUS;
import com.linkedin.backend.features.networking.model.Connection;
import com.linkedin.backend.features.networking.repository.ConnectionRepository;
import com.linkedin.backend.features.networking.service.ConnectionService;
import com.linkedin.backend.features.notifications.service.NotificationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ConnectionServiceImpl implements ConnectionService {
    ConnectionRepository connectionRepository;
    AuthenticationUserService userService;
    NotificationService notificationService;
    FollowService followService;


    @Override
    public List<Connection> getUserConnections(User user, CONNECTION_STATUS status) {
        return connectionRepository.cFindConnectionsByUserAndStatus(user.getId(), status);
    }

    @Override
    public Connection sendConnectionRequest(User author, Long recipientId) {
        User recipient = userService.getUserById(recipientId);
        if (connectionRepository.existsByAuthorAndRecipient(author, recipient) ||
            connectionRepository.existsByRecipientAndAuthor(author, recipient)) {
            log.warn("Connection request already exists between {} and {}", author.getId(), recipient.getId());
            throw new AppException("Connection request already exists between the users");
        }

        Connection connection = new Connection();
        connection.setAuthor(author);
        connection.setRecipient(recipient);
        connection = connectionRepository.save(connection);

        notificationService.sendNewInvitationToUser(author, recipient, connection);

        followService.followUser(author, recipient.getId());
        return connection;
    }

    @Override
    public Connection acceptConnectionRequest(User recipient, Long connectionId) {
        Connection connection = getConnectionById(connectionId);
        ensureUserCanModify(connection, recipient, ConnectionServiceImpl.Role.RECIPIENT);

        if (connection.getStatus().equals(CONNECTION_STATUS.ACCEPTED)) {
            log.warn("Connection {} is already accepted", connectionId);
            throw new AppException("Connection request is already accepted");
        }

        connection.setStatus(CONNECTION_STATUS.ACCEPTED);
        connection = connectionRepository.save(connection);
        notificationService.sendInvitationAcceptedNotification(recipient, connection.getAuthor(), connection);

        followService.followUser(recipient, connection.getAuthor().getId());
        return connection;
    }

    @Override
    public Connection rejectOrCancelConnection(User user, Long connectionId) {
        Connection connection = getConnectionById(connectionId);
        ensureUserCanModify(connection, user, Role.RELATED);
        connectionRepository.delete(connection);

        notificationService.sendInvitationRejectedOrCancelNotification(
                connection.getAuthor(), connection.getRecipient(), connection
        );

        Long authorId = connection.getAuthor().getId();
        Long recipientId = connection.getRecipient().getId();

        // Luôn unfollow từ người gửi đến người nhận
        followService.unfollowUser(authorId, recipientId);

        // Nếu là quan hệ đã ACCEPTED thì cần unfollow ngược lại
        if (connection.getStatus() == CONNECTION_STATUS.ACCEPTED) {
            followService.unfollowUser(recipientId, authorId);
        }

        return connection;
    }


    @Override
    public Connection markConnectionAsSeen(User recipient, Long connectionId) {
        Connection connection = getConnectionById(connectionId);
        ensureUserCanModify(connection, recipient, ConnectionServiceImpl.Role.RECIPIENT);
        connection.setSeen(true);
        connection = connectionRepository.save(connection);
        notificationService.sendConnectionSeenNotification(recipient, connection.getAuthor(), connection);
        return connection;
    }

    private Connection getConnectionById(Long connectionId) {
        return connectionRepository.findById(connectionId)
                .orElseThrow(() -> new AppException("Connection not found"));
    }

    enum Role { AUTHOR, RECIPIENT, RELATED }

    private void ensureUserCanModify(Connection connection, User user, Role role) {
        if (role == Role.AUTHOR) {
            if (connection.getAuthor() == null || !connection.getAuthor().getId().equals(user.getId())) {
                throw new AppException("You (Author) are not authorized to modify this connection request");
            }
        } else if (role == Role.RECIPIENT){
            if (connection.getRecipient() == null || !connection.getRecipient().getId().equals(user.getId())) {
                throw new AppException("You (Recipient) are not authorized to modify this connection request");
            }
        }else {
            if(!connection.getAuthor().getId().equals(user.getId()) && !connection.getRecipient().getId().equals(user.getId())) {
                throw new AppException("You are not authorized to modify this connection request");
            }
        }
    }

}
