package com.linkedin.backend.controller;

import com.linkedin.backend.dto.ApiResponse;
import com.linkedin.backend.dto.OnlineUserDto;
import com.linkedin.backend.features.authentication.model.User;
import com.linkedin.backend.features.networking.domain.CONNECTION_STATUS;
import com.linkedin.backend.features.networking.model.Connection;
import com.linkedin.backend.features.networking.service.ConnectionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping
@RequiredArgsConstructor
public class OnlineUserController {
    private final RedisTemplate<String, String> redisTemplate;
    private final ConnectionService connectionService;

    @PostMapping("/online-users")
    public ApiResponse<List<OnlineUserDto>> fetchOnlineUsers(@RequestAttribute("authenticatedUser") User authenticatedUser) {
        List<Long> connectionUserIds = connectionService.getConnectionUserIds(authenticatedUser.getId(), CONNECTION_STATUS.ACCEPTED);
        List<OnlineUserDto> onlineUsers = new ArrayList<>();
        for (Long userId : connectionUserIds) {
            Boolean isOnline = redisTemplate.hasKey("online:" + userId);
            OnlineUserDto onlineUserDto = new OnlineUserDto();
            onlineUserDto.setId(userId);
            onlineUserDto.setIsOnline(isOnline);
            String lastOnlineStr = redisTemplate.opsForValue().get("last_online:" + userId);
            onlineUserDto.setLastOnline(lastOnlineStr);
            onlineUsers.add(onlineUserDto);
        }
        return ApiResponse.<List<OnlineUserDto>>builder()
                .message("Retrieved online users successfully")
                .data(onlineUsers)
                .build();
    }
}
