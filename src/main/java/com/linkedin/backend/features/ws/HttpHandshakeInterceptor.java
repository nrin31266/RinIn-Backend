package com.linkedin.backend.features.ws;

import com.linkedin.backend.features.authentication.service.AuthenticationUserService;
import com.linkedin.backend.features.authentication.utils.JsonWebToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;
@Slf4j
public class HttpHandshakeInterceptor implements HandshakeInterceptor {

    private final JsonWebToken jsonWebToken;
    private final AuthenticationUserService authenticationUserService;

    public HttpHandshakeInterceptor(JsonWebToken jsonWebToken, AuthenticationUserService authenticationUserService) {
        this.jsonWebToken = jsonWebToken;
        this.authenticationUserService = authenticationUserService;
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        if(request instanceof ServletServerHttpRequest serverHttpRequest){
            String token = serverHttpRequest.getServletRequest().getParameter("token");




            log.warn("WebSocket Handshake initiated with token: {}.", token);
            if(token != null && !token.isEmpty()) {
                // Validate the token
                try {
                    jsonWebToken.isTokenExpired(token);
                    var email = jsonWebToken.getEmailFromToken(token);
                    var user = authenticationUserService.getUser(email);
                    if (user != null) {
                        // Set userId in attributes for later use
                        attributes.put("userId", String.valueOf(user.getId()));
                        log.warn("User {} connected via WebSocket", user.getId());
                    } else {
                        log.error("User not found for email: {}", email);
                        return false; // Reject the handshake if user not found
                    }
                } catch (Exception e) {
                    log.error("Invalid token: {}", e.getMessage());
                    return false; // Reject the handshake if token is invalid
                }
            }
        }
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {

    }
}
