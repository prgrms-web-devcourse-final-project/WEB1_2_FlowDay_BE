package org.example.flowday.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * 클라이언트가 서버에 소켓 연결할 때 경로 : /connect/websocket
 * 클라이언트가 서버에 구독요청할 경로 : /topic/rooms/{roomId}
 * 채팅 메세지 보낼 때 요청할 경로 : /chat/{roomId} + message body
 */
@Configuration
@EnableWebSocketMessageBroker
public class StompWebSocketConfig implements WebSocketMessageBrokerConfigurer {
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/connect/websocket", "/ws-stomp")
                // TODO: 구체적인 경로로 수정
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }
}
