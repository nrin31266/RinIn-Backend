package com.linkedin.backend.features.ws;

import com.linkedin.backend.dto.OnlineUserDto;
import com.linkedin.backend.features.notifications.service.NotificationService;
import com.linkedin.backend.features.notifications.service.OnlineNotificationService;
import com.linkedin.backend.features.redis.OnlineStatusService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.time.LocalDateTime;

@Slf4j
@Component
public class WebSocketDisconnectListener implements ApplicationListener<SessionDisconnectEvent> {

    @Autowired
    private OnlineStatusService onlineStatusService;

    @Autowired
    private OnlineNotificationService onlineNotificationService;

    @Override
    public void onApplicationEvent(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());

        String userId = (String) accessor.getSessionAttributes().get("userId");

        if (userId != null) {
            log.warn("User {} disconnected", userId);
            onlineStatusService.markOffline(userId);
            onlineNotificationService.sendOnlineStatusUpdate(
                    OnlineUserDto.builder()
                            .id(Long.parseLong(userId))
                            .isOnline(false)
                            .lastOnline(LocalDateTime.now().toString())
                            .build()
            );
        } else {
            log.warn("Disconnect event received but userId is null");
        }
    }
}
