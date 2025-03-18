package com.linkedin.backend.features.message.service;

import com.linkedin.backend.exception.AppException;
import com.linkedin.backend.features.authentication.model.User;
import com.linkedin.backend.features.message.model.Conversation;
import com.linkedin.backend.features.message.model.Message;
import com.linkedin.backend.features.message.repository.ConversationRepository;
import com.linkedin.backend.features.message.repository.MessageRepository;
import com.linkedin.backend.features.notifications.repository.NotificationRepository;
import com.linkedin.backend.features.notifications.service.NotificationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MessagingService {
    ConversationRepository conversationRepository;
    MessageRepository messageRepository;
    NotificationService notificationService;


    public List<Conversation> getConversationsOfUser(User user) {
        return conversationRepository.findByAuthorOrRecipient(user, user);

    }

    public Conversation getConversation(User authenticatedUser, Long conversationId) {
        Conversation conversation = conversationRepository.customFindByIdWithMessages(conversationId)
                .orElseThrow(() -> new AppException("Conversation with ID " + conversationId + " not found"));

        if (!hasAccessToConversation(authenticatedUser, conversation)) {
            throw new AppException("You are not authorized to view this conversation");
        }


        return conversation;

    }
    private boolean hasAccessToConversation(User user, Conversation conversation) {
        return user.getId().equals(conversation.getAuthor().getId()) ||
               user.getId().equals(conversation.getRecipient().getId());
    }



    @Transactional
    public Conversation createConversationAndAddMessage(User sender, User receiver, String content) {
        conversationRepository.findByAuthorAndRecipient(sender, receiver).ifPresentOrElse(conversation -> {
            throw new AppException("This conversation already exists");
        }, () -> {

        });
        conversationRepository.findByAuthorAndRecipient(receiver, sender).ifPresentOrElse(conversation -> {
            throw new AppException("This conversation already exists");
        }, () -> {

        });

        Conversation newConversation = new Conversation();
        newConversation.setAuthor(sender);
        newConversation.setRecipient(receiver);
        newConversation = conversationRepository.save(newConversation);

        Message newMessage = new Message();
        newMessage.setSender(sender);
        newMessage.setReceiver(receiver);
        newMessage.setContent(content);
        newMessage.setConversation(newConversation);
        newMessage = messageRepository.save(newMessage);

        newConversation.getMessages().add(newMessage);

        notificationService.sendConversationToUsers(sender.getId(), receiver.getId(), newConversation);

        return newConversation;
    }

    public Message addMessageToConversation(Long conversationId, User sender, User receiver, String content) {
        Conversation conversation = conversationRepository.findById(conversationId).orElseThrow(() -> new AppException("Conversation with ID " + conversationId + " not found"));
        if(!hasAccessToConversation(sender, conversation)) {
            throw new AppException("You are not authorized to view this conversation");
        }
        if(!hasAccessToConversation(receiver, conversation)) {
            throw new AppException("Recipient does not belong to this conversation");
        }
        Message newMessage = new Message();
        newMessage.setSender(sender);
        newMessage.setReceiver(receiver);
        newMessage.setContent(content);
        newMessage.setConversation(conversation);
        newMessage = messageRepository.save(newMessage);

        conversation.getMessages().add(newMessage);

        notificationService.sendMessageToConversation(conversation.getId(), newMessage);
        notificationService.sendConversationToUsers(sender.getId(), receiver.getId(), conversation);

        return newMessage;
    }

    public Message markMessageAsRead(User user, Long messageId) {
        Message message = messageRepository.findById(messageId).orElseThrow(() -> new AppException("Message with ID " + messageId + " not found"));
        if(!user.getId().equals(message.getReceiver().getId())) {
            throw new AppException("You are not authorized to mark message as read this conversation");
        }
        message.setIsRead(true);
        return messageRepository.save(message);
    }
}
