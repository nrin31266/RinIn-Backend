package com.linkedin.backend.features.message.controller;

import com.linkedin.backend.dto.ApiResponse;
import com.linkedin.backend.dto.PageableDto;
import com.linkedin.backend.features.authentication.model.User;
import com.linkedin.backend.features.authentication.service.AuthenticationUserService;
import com.linkedin.backend.features.message.dto.CheckConversationDto;
import com.linkedin.backend.features.message.dto.ConversationDetailsDto;
import com.linkedin.backend.features.message.dto.ConversationDto;
import com.linkedin.backend.features.message.dto.MessageDto;
import com.linkedin.backend.features.message.model.Conversation;
import com.linkedin.backend.features.message.model.Message;
import com.linkedin.backend.features.message.service.MessagingService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/messaging")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MessagingController {
    MessagingService messageService;
    AuthenticationUserService authenticationUserService;

    @GetMapping("/conversations")
    public ApiResponse<List<ConversationDto>> conversation(@RequestAttribute("authenticatedUser") User user) {
        return ApiResponse.<List<ConversationDto>>builder().
                data(messageService.getConversationsOfUser(user))
                .build();
    }
    @GetMapping("/conversations/{conversationId}")
    public ApiResponse<ConversationDetailsDto> conversation(@RequestAttribute("authenticatedUser") User user, @PathVariable Long conversationId) {
        return ApiResponse.<ConversationDetailsDto>builder().
                data(messageService.getConversation(user, conversationId))
                .build();
    }

    @PostMapping("/conversations")
    public ApiResponse<Conversation> createConversationAndAddMessage(@RequestBody MessageDto requestBody, @RequestAttribute("authenticatedUser")User sender ) {
        User receiver = authenticationUserService.getUserById(requestBody.getReceiverId());
        return ApiResponse.<Conversation>builder().
                data(messageService.createConversationAndAddMessage(sender,receiver,  requestBody.getContent()))
                .build();

    }

    @PostMapping("/conversations/{conversationId}/message")
    public ApiResponse<Message> addMessageToConversation(@RequestAttribute("authenticatedUser") User sender,@PathVariable Long conversationId, @RequestBody MessageDto requestBody) {
        return ApiResponse.<Message>builder()
                .data(messageService.addMessageToConversation(conversationId, sender, requestBody))
                .build();
    }

    @PutMapping("/conversations/{conversationId}/mark-as-read")
    public ApiResponse<Void> markConversationAsRead(@PathVariable Long conversationId, @RequestAttribute("authenticatedUser") User user) {
        messageService.markConversationAsRead(user, conversationId);
        return ApiResponse.<Void>builder()
                .message("Conversation marked as read successfully")
                .build();
    }
    @GetMapping("/conversations/with/{userId}")
    public ApiResponse<CheckConversationDto> hasConversationWithUser(@RequestAttribute("authenticatedUser") User user, @PathVariable Long userId) {
        User otherUser = authenticationUserService.getUserById(userId);
        return ApiResponse.<CheckConversationDto>builder()
                .data(messageService.hasConversationWithUser(user, otherUser))
                .build();
    }

    @GetMapping("/conversations/{conversationId}/messages")
    public ApiResponse<PageableDto<Message>> getMessagesInConversation(@PathVariable Long conversationId,
                                                                       @RequestAttribute("authenticatedUser") User user,
                                                                       @RequestParam(value = "beforeTime", required = false) LocalDateTime beforeTime,
                                                                       @RequestParam(value = "limit", defaultValue = "20") int limit
    ) {
        return ApiResponse.<PageableDto<Message>>builder()
                .data(messageService.getMessagesBeforeTime(conversationId, beforeTime, limit, user))
                .build();
    }
}
