package com.linkedin.backend.features.redis;

import com.linkedin.backend.dto.OnlineUserDto;
import com.linkedin.backend.features.authentication.service.AuthenticationUserService;
import com.linkedin.backend.features.notifications.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
public class OnlineStatusService {
    final Duration TTL = Duration.ofMinutes(2);
    RedisTemplate<String, String> redisTemplate;
    AuthenticationUserService authenticationUserService;


    public void markOnline(String userId) {
        redisTemplate.opsForValue().set("online:" + userId, "1", TTL);

    }
    public void markOffline(String userId) {
        redisTemplate.delete("online:" + userId);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime lastOnline = now;

        String lastOnlineStr = redisTemplate.opsForValue().get("last_online:" + userId);
        if (lastOnlineStr != null) {
            try {
                lastOnline = LocalDateTime.parse(lastOnlineStr);
            } catch (Exception ignored) {
            }
        }

        if (Duration.between(lastOnline, now).toHours() > 24) {
            authenticationUserService.updateLastLogin(Long.parseLong(userId));
        }

        redisTemplate.opsForValue().set("last_online:" + userId, now.toString(), Duration.ofDays(2));

    }

    public boolean isOnline(String userId) {
        return redisTemplate.hasKey("online:" + userId);
    }
}
