package com.linkedin.backend.features.ws;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;

public class HandshakeHandler extends DefaultHandshakeHandler {
    @Override
    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        // Lấy userId từ attributes (set từ HandshakeInterceptor)
        String userId = (String) attributes.get("userId");

        // Trả về 1 Principal tùy chỉnh
        return () -> userId;
    }
}
