package com.cloudmessenger.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * Конфигурация WebSocket для реальновременного обмена сообщениями
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Префикс для обращения к брокеру сообщений
        config.enableSimpleBroker("/topic");
        // Префикс для обращения к контроллерам обработки сообщений
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Регистрируем точку подключения WebSocket с поддержкой SockJS для старых браузеров
        registry.addEndpoint("/ws")
                .setAllowedOrigins("*")
                .withSockJS();
    }
} 