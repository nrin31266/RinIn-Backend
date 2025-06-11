package com.linkedin.backend.features.notifications.service;


import com.linkedin.backend.exception.AppException;
import com.linkedin.backend.features.authentication.model.User;
import com.linkedin.backend.features.feed.model.Comment;
import com.linkedin.backend.features.message.model.Conversation;
import com.linkedin.backend.features.message.model.Message;
import com.linkedin.backend.features.networking.model.Connection;
import com.linkedin.backend.features.notifications.domain.NotificationType;
import com.linkedin.backend.features.notifications.dto.MessageDto;
import com.linkedin.backend.features.notifications.model.Notification;
import com.linkedin.backend.features.notifications.repository.NotificationRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationService {
    NotificationRepository notificationRepository;
    SimpMessagingTemplate messagingTemplate;

    public List<Notification> getUserNotification(User user) {
        return notificationRepository.findByRecipientOrderByCreationDateDesc(user);
    }

    public Notification markNotificationAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId).orElseThrow(() -> new AppException("Notification not found"));

        notification.setIsRead(true);
        messagingTemplate.convertAndSend("/topic/users/" + notification.getRecipient().getId(), notification);

        return notificationRepository.save(notification);
    }

    public void sendLikeNotification(User author, User recipient, Long postId) {
        if (author.getId().equals(recipient.getId())) {
            return;
        }
        Notification notification = Notification.builder()
                .actor(author)
                .recipient(recipient)
                .type(NotificationType.LIKE)
                .resourceId(postId)
                .build();
        notificationRepository.save(notification);

        messagingTemplate.convertAndSend("/topic/users/" + recipient.getId() + "/notifications", notification);
    }

    public void sendLikeToPost(Long postId, Set<User> likes) {
        messagingTemplate.convertAndSend("/topic/likes/" + postId, likes);
    }

    public void sendCommentNotification(User author, User recipient, Long postId) {
        if (author.getId().equals(recipient.getId())) {
            return;
        }
        Notification notification = Notification.builder()
                .actor(author)
                .recipient(recipient)
                .type(NotificationType.COMMENT)
                .resourceId(postId)
                .build();
        notificationRepository.save(notification);

        messagingTemplate.convertAndSend("/topic/users/" + recipient.getId() + "/notifications", notification);
    }

    public void sendCommentToPost(Long postId, Comment comment) {
        messagingTemplate.convertAndSend("/topic/comments/" + postId, comment);
    }

    public void sendConversationToUsers(Long senderId, Long receiverId, Conversation newConversation) {
        messagingTemplate.convertAndSend("/topic/users/" + senderId + "/conversations", newConversation);
        messagingTemplate.convertAndSend("/topic/users/" + receiverId + "/conversations", newConversation);

    }

    public void sendMessageToConversation(Long conversationId, Message newMessage) {
        messagingTemplate.convertAndSend("/topic/conversations/" + conversationId + "/messages", newMessage);
    }

    public void sendReadToMessage(Long messageId, MessageDto messageDto) {
        messagingTemplate.convertAndSend("/topic/messages/" + messageId, messageDto);
    }

    public void sendNewInvitationToUser(User author, User recipient, Connection connection) {
        messagingTemplate.convertAndSend("/topic/users/" + recipient.getId() + "/connections/new", connection);
        messagingTemplate.convertAndSend("/topic/users/" + author.getId() + "/connections/new", connection);
    }

    public void sendInvitationAcceptedNotification(User recipient, User author, Connection connection) {
        messagingTemplate.convertAndSend("/topic/users/" + recipient.getId() + "/connections/accepted", connection);
        messagingTemplate.convertAndSend("/topic/users/" + author.getId() + "/connections/accepted", connection);
    }

    public void sendInvitationRejectedOrCancelNotification(User recipient, User author, Connection connection) {
        messagingTemplate.convertAndSend("/topic/users/" + recipient.getId() + "/connections/rejected", connection);
        messagingTemplate.convertAndSend("/topic/users/" + author.getId() + "/connections/rejected", connection);
    }

    public void sendConnectionSeenNotification(User recipient, User author, Connection connection) {
        messagingTemplate.convertAndSend("/topic/users/" + recipient.getId() + "/connections/seen", connection);
        messagingTemplate.convertAndSend("/topic/users/" + author.getId() + "/connections/seen", connection);
    }
}
