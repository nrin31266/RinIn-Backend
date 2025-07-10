package com.linkedin.backend.features.ws;

import com.linkedin.backend.features.authentication.service.AuthenticationUserService;
import com.linkedin.backend.features.authentication.utils.JsonWebToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;


@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfiguration implements WebSocketMessageBrokerConfigurer {

    @Autowired
    private JsonWebToken jsonWebToken;
    @Autowired
    private AuthenticationUserService authenticationUserService;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .addInterceptors(new HttpHandshakeInterceptor(jsonWebToken, authenticationUserService))
                .setHandshakeHandler(new HandshakeHandler());

    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes("/app");
        registry.enableSimpleBroker("/topic");
    }
}
