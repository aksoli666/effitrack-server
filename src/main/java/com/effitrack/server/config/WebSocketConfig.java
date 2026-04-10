package com.effitrack.server.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import static com.effitrack.server.constant.StringConst.APPLICATION_DESTINATION_PREFIX;
import static com.effitrack.server.constant.StringConst.BROKER_DESTINATION_PREFIX;
import static com.effitrack.server.constant.StringConst.SYMBOL_ASTERISK;
import static com.effitrack.server.constant.StringConst.WEBSOCKET_ENDPOINT;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker(BROKER_DESTINATION_PREFIX);
        config.setApplicationDestinationPrefixes(APPLICATION_DESTINATION_PREFIX);
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint(WEBSOCKET_ENDPOINT).setAllowedOriginPatterns(SYMBOL_ASTERISK).withSockJS();
    }
}
