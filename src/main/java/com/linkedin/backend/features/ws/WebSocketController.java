package com.linkedin.backend.features.ws;

import com.linkedin.backend.features.redis.OnlineStatusService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
public class WebSocketController {
    OnlineStatusService onlineStatusService;
    @MessageMapping("/ping")
    public void handlePing(Principal principal) {

        String userId = principal != null ? principal.getName() : null;

        if (userId != null && !userId.isEmpty()) {
            log.info("Ping từ userId: {}", userId);
            onlineStatusService.markOnline(userId); // Reset TTL
        } else {
            log.warn("Ping không có userId header");
        }
    }
}
