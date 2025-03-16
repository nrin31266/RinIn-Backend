package com.linkedin.backend.features.notifications.controller;

import com.linkedin.backend.dto.ApiResponse;
import com.linkedin.backend.features.authentication.model.User;
import com.linkedin.backend.features.notifications.model.Notification;
import com.linkedin.backend.features.notifications.service.NotificationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationController {
    NotificationService notificationService;

    @GetMapping
    public ApiResponse<List<Notification>> getUserNotifications(
            @RequestAttribute("authenticatedUser") User user
    ) {
        return ApiResponse.<List<Notification>>builder()
                .data(notificationService.getUserNotification(user))
                .build();
    }


    @PutMapping("/{notificationId}")
    public ApiResponse<Notification> updateUserNotificationAsRead(@PathVariable Long notificationId){
        return ApiResponse.<Notification>builder()
                .data(notificationService.markNotificationAsRead(notificationId))
                .build();
    }
}
