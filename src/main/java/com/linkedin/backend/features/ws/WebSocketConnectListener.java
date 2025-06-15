package com.linkedin.backend.features.ws;

import com.linkedin.backend.dto.OnlineUserDto;
import com.linkedin.backend.features.notifications.service.NotificationService;
import com.linkedin.backend.features.redis.OnlineStatusService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;

@Component
@Slf4j
public class WebSocketConnectListener  implements ApplicationListener<SessionConnectEvent> {
    @Autowired
    private OnlineStatusService onlineStatusService;

    @Autowired
    private NotificationService notificationService;

    @Override
    public void onApplicationEvent(SessionConnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String userId = accessor.getFirstNativeHeader("userId");
        if (userId != null && !userId.isEmpty()) {
            onlineStatusService.markOnline(userId);
            notificationService.sendOnlineStatusUpdate(
                    OnlineUserDto.builder()
                            .id(Long.parseLong(userId))
                            .isOnline(true)
                            .lastOnline(null)
                            .build()
            );
            log.warn("User {} connected", userId);
        }
    }
}
