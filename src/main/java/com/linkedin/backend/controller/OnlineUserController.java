package com.linkedin.backend.controller;

import com.linkedin.backend.dto.ApiResponse;
import com.linkedin.backend.dto.OnlineUserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/online-users")
@RequiredArgsConstructor
public class OnlineUserController {
    private final RedisTemplate<String, String> redisTemplate;
    @PostMapping("/by-ids")
    public ApiResponse<List<OnlineUserDto>> fetchOnlineUsersByIds(@RequestBody List<Long> userIds) {
        List<OnlineUserDto> onlineUsers = new ArrayList<>();
        for (Long id : userIds) {
            Boolean isOnline = redisTemplate.hasKey("online:" + id);
            OnlineUserDto onlineUserDto = new OnlineUserDto();
            onlineUserDto.setId(id);
            onlineUserDto.setIsOnline(isOnline);
            String lastOnlineStr = redisTemplate.opsForValue().get("last_online:" + id);
            onlineUserDto.setLastOnline(lastOnlineStr);
            onlineUsers.add(onlineUserDto);
        }
        return ApiResponse.<List<OnlineUserDto>>builder()
                .message("Retrieved online users successfully")
                .data(onlineUsers)
                .build();
    }
}
