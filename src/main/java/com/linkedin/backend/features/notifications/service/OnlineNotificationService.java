package com.linkedin.backend.features.notifications.service;

import com.linkedin.backend.dto.OnlineUserDto;
import com.linkedin.backend.features.networking.domain.CONNECTION_STATUS;
import com.linkedin.backend.features.networking.service.ConnectionService;
import com.linkedin.backend.features.notifications.repository.NotificationRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OnlineNotificationService {
    NotificationRepository notificationRepository;
    SimpMessagingTemplate messagingTemplate;
    ConnectionService connectionService;
    private final RedisTemplate<String, String> redisTemplate;

    public void sendOnlineStatusUpdate(OnlineUserDto onlineUserDto) {
        List<Long> connectionUserIds = connectionService.getConnectionUserIds(onlineUserDto.getId(), CONNECTION_STATUS.ACCEPTED);

        for (Long userId : connectionUserIds) {

            boolean isOnline = redisTemplate.hasKey("online:" + userId);

            if (!isOnline) {
                continue; // Chỉ gửi nếu người nhận đang online
            }

            messagingTemplate.convertAndSendToUser(
                    String.valueOf(userId),
                    "/queue/online-status",
                    onlineUserDto
            );
        }
    }
}
