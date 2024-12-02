package org.example.flowday.domain.chat.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

@Slf4j
@Component
public class StompWebSocketEventHandler {
    @EventListener
    public void handleWebSocketSessionConnectEvent(SessionConnectEvent event) {
        log.info("세션(클라이언트)가 연결을 시도합니다...");
    }

    @EventListener
    public void handleWebSocketSessionConnectedEvent(SessionConnectedEvent event) {
        log.info("세션(클라이언트)이 연결되었습니다. " + event.getMessage());
    }

    @EventListener
    public void handleWebSocketSessionSubscribeEvent(SessionSubscribeEvent event) {
        log.info("세션(클라이언트)이 토픽을 구독하였습니다.");
    }

    @EventListener
    public void handleWebSocketSessionUnsubscribeEvent(SessionUnsubscribeEvent event) {
        log.info("세션(클라이언트)가 구독을 취소하였습니다.");
    }

    @EventListener
    public void handleWebSocketSessionDisconnectEvent(SessionDisconnectEvent event) {
        log.info("세션(클라이언트) 연결이 종료되었습니다.");
    }
}