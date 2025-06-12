package com.linkedin.backend.features.message.service;

import com.linkedin.backend.exception.AppException;
import com.linkedin.backend.features.authentication.model.User;
import com.linkedin.backend.features.authentication.service.AuthenticationUserService;
import com.linkedin.backend.features.message.dto.CheckConversationDto;
import com.linkedin.backend.features.message.dto.ConversationDetailsDto;
import com.linkedin.backend.features.message.dto.ConversationDto;
import com.linkedin.backend.features.message.dto.MessageDto;
import com.linkedin.backend.features.message.model.Conversation;
import com.linkedin.backend.features.message.model.ConversationParticipant;
import com.linkedin.backend.features.message.model.Message;
import com.linkedin.backend.features.message.repository.ConversationParticipantRepository;
import com.linkedin.backend.features.message.repository.ConversationRepository;
import com.linkedin.backend.features.message.repository.MessageRepository;
import com.linkedin.backend.features.notifications.service.NotificationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MessagingService {
    ConversationRepository conversationRepository;
    MessageRepository messageRepository;
    NotificationService notificationService;
    ConversationParticipantRepository conversationParticipantRepository;
    AuthenticationUserService authenticationUserService;




    public List<ConversationDto> getConversationsOfUser(User user) {
        Long userId = user.getId();
        return conversationRepository.findAllConversations(userId);
    }


    public ConversationDetailsDto getConversation(User authenticatedUser, Long conversationId) {
        // 1. Lấy DTO từ custom query
        ConversationDetailsDto conversationDetailsDto = conversationRepository
                .findConversationById(conversationId, authenticatedUser.getId())
                .orElseThrow(() -> new AppException("Conversation with ID " + conversationId + " not found"));

        boolean isParticipant = conversationParticipantRepository
                .existsByUserIdAndConversationId(authenticatedUser.getId(), conversationId);

        if (!isParticipant) {
            throw new AppException("You are not a participant in this conversation");
        }

        return conversationDetailsDto;
    }





    @Transactional
    public Conversation createConversationAndAddMessage(User sender, User receiver, String content) {
        Optional<Conversation> existing = conversationRepository
                .findOneToOneConversationBetweenUsers(sender, receiver);
        if (existing.isPresent()) {
            throw new AppException("Conversation already exists between these users");
        }

        // 1. Tạo conversation
        Conversation conversation = new Conversation();
        conversation.setCreatedBy(sender);
        conversation.setIsGroup(false);

        // 2. Tạo message đầu tiên
        Message message = new Message();
        message.setSender(sender);
        message.setConversation(conversation);
        message.setContent(content);
        conversation.setMessages(List.of(message));

        // 3. Gán message này là lastMessage
        conversation.setLastMessage(message);

        // 4. Tạo participant cho cả 2 người
        ConversationParticipant senderParticipant = new ConversationParticipant();
        senderParticipant.setUser(sender);
        senderParticipant.setConversation(conversation);
        senderParticipant.setUnreadCount(0); // Vì chính mình gửi
        senderParticipant.setLastReadAt(LocalDateTime.now());

        ConversationParticipant receiverParticipant = new ConversationParticipant();
        receiverParticipant.setUser(receiver);
        receiverParticipant.setConversation(conversation);
        receiverParticipant.setUnreadCount(1); // Vì chưa đọc
        receiverParticipant.setLastReadAt(null); // hoặc set thời điểm rất cũ
        conversation.setParticipants(List.of(senderParticipant, receiverParticipant));

        // 5. Gửi thông báo
        notificationService.sendConversationToUsers(sender.getId(), receiver.getId(), conversation);
        notificationService.sendMessageToConversation(conversation.getId(), message);
        
        return conversationRepository.save(conversation);
    }


    @Transactional
    public Message addMessageToConversation(Long conversationId, User sender, MessageDto messageDto) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new AppException("Conversation with ID " + conversationId + " not found"));

        boolean isParticipant = conversation.getParticipants().stream()
                .anyMatch(p -> p.getUser().getId().equals(sender.getId()));
        if (!isParticipant) {
            throw new AppException("You are not a participant in this conversation");
        }

        Message newMessage = new Message();
        newMessage.setSender(sender);
        newMessage.setConversation(conversation);
        newMessage.setContent(messageDto.getContent());

        // Gán lại lastMessage
        conversation.setLastMessage(newMessage);

        // Cập nhật participant
        for (ConversationParticipant participant : conversation.getParticipants()) {
            if (participant.getUser().getId().equals(sender.getId())) {
                participant.setUnreadCount(0);
                participant.setLastReadAt(LocalDateTime.now());
            } else {
                int currentUnread = participant.getUnreadCount() != null ? participant.getUnreadCount() : 0;
                participant.setUnreadCount(currentUnread + 1);
            }
        }

        // Gửi notification
        notificationService.sendMessageToConversation(conversation.getId(), newMessage);

        // Save message (nếu cascade thì tự lưu conversation & participant)
        return messageRepository.save(newMessage);
    }
    @Transactional
    public void markConversationAsRead(User user, Long conversationId) {
        ConversationParticipant participant = conversationParticipantRepository
                .findByUserIdAndConversationId(user.getId(), conversationId)
                .orElseThrow(() -> new AppException("You are not part of this conversation"));

        participant.setUnreadCount(0);
        participant.setLastReadAt(LocalDateTime.now());
        participant = conversationParticipantRepository.save(participant);

        notificationService.sendReadToConversation(conversationId, user.getId(), participant);
    }





    public CheckConversationDto hasConversationWithUser(User user, User otherUser) {
        Optional<Conversation> existingConversation = conversationRepository
                .findOneToOneConversationBetweenUsers(user, otherUser);
        return CheckConversationDto.builder()
                .isConversationExists(existingConversation.isPresent())
                .receiver(otherUser)
                .conversationId(existingConversation.map(Conversation::getId).orElse(null))
                .build();
    }


}
