package com.market.quotesservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

//    private final JwtChannelInterceptor jwtChannelInterceptor;

//    public WebSocketConfig(JwtChannelInterceptor jwtChannelInterceptor) {
//    this.jwtChannelInterceptor = jwtChannelInterceptor;
//    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-quotes")
                .setAllowedOrigins("http://localhost:5173");
/*                .withSockJS();*/
    }
//
//    @Override
//    public void configureClientInboundChannel(ChannelRegistration registration) {
//        // Hook in the interceptor
////        registration.taskExecutor().keepAliveSeconds(10);
//        registration.interceptors(jwtChannelInterceptor);
//    }
}
