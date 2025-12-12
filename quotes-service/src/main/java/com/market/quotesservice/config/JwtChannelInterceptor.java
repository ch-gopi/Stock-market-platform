package com.market.quotesservice.config;

import org.springframework.messaging.Message;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;

import java.security.Principal;
import java.util.Collections;

@Component
public class JwtChannelInterceptor implements ChannelInterceptor {

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            // Expect the gateway to inject a trusted header
            String userId = accessor.getFirstNativeHeader("X-User-Id");
            if (userId == null || userId.isBlank()) {
                throw new IllegalArgumentException("Missing gateway-authenticated user header");
            }

            // Create a Principal for the STOMP session
            Principal user = new UsernamePasswordAuthenticationToken(userId, null, Collections.emptyList());
            accessor.setUser(user);
        }

        return message;
    }
}
