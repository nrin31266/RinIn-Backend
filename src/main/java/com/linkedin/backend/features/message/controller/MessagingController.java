package com.linkedin.backend.features.message.controller;

import com.linkedin.backend.dto.ApiResponse;
import com.linkedin.backend.features.authentication.model.User;
import com.linkedin.backend.features.authentication.service.AuthenticationUserService;
import com.linkedin.backend.features.message.dto.MessageDto;
import com.linkedin.backend.features.message.model.Conversation;
import com.linkedin.backend.features.message.model.Message;
import com.linkedin.backend.features.message.service.MessagingService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/messaging")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MessagingController {
    MessagingService messageService;
    AuthenticationUserService authenticationUserService;

    @GetMapping("/conversations")
    public ApiResponse<List<Conversation>> conversation(@RequestAttribute("authenticatedUser") User user) {
        return ApiResponse.<List<Conversation>>builder().
                data(messageService.getConversationsOfUser(user))
                .build();
    }
    @GetMapping("/conversations/{conversationId}")
    public ApiResponse<Conversation> conversation(@RequestAttribute("authenticatedUser") User user, @PathVariable Long conversationId) {
        return ApiResponse.<Conversation>builder().
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
        User receiver = authenticationUserService.getUserById(requestBody.getReceiverId());
        return ApiResponse.<Message>builder()
                .data(messageService.addMessageToConversation(conversationId, sender, receiver, requestBody.getContent()))
                .build();
    }

    @PutMapping("/conversations/messages/{messageId}")
    public ApiResponse<Message> markMessageAsRead(@PathVariable Long messageId, @RequestAttribute("authenticatedUser") User user) {
        return ApiResponse.<Message>builder()
                .data(messageService.markMessageAsRead(user, messageId))
                .build();

    }
}
