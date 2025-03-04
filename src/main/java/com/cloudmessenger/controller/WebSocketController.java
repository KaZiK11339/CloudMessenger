package com.cloudmessenger.controller;

import com.cloudmessenger.model.Message;
import com.cloudmessenger.model.User;
import com.cloudmessenger.service.FirebaseService;
import com.cloudmessenger.service.MessageService;
import com.cloudmessenger.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

/**
 * Контроллер для обработки WebSocket сообщений
 */
@Controller
public class WebSocketController {

    private final MessageService messageService;
    private final UserService userService;
    private final FirebaseService firebaseService;
    private final SimpMessagingTemplate messagingTemplate;
    
    @Autowired
    public WebSocketController(MessageService messageService, 
                               UserService userService,
                               FirebaseService firebaseService,
                               SimpMessagingTemplate messagingTemplate) {
        this.messageService = messageService;
        this.userService = userService;
        this.firebaseService = firebaseService;
        this.messagingTemplate = messagingTemplate;
    }
    
    /**
     * Обрабатывает отправку личных сообщений через WebSocket
     */
    @MessageMapping("/chat.sendMessage")
    public void sendMessage(WebSocketMessage webSocketMessage, Principal principal) {
        // Получаем отправителя из Spring Security Principal
        User sender = userService.findByEmail(principal.getName());
        User receiver = userService.findById(webSocketMessage.getReceiverId());
        
        if (sender != null && receiver != null) {
            // Создаем сообщение
            Message message = new Message();
            message.setSender(sender);
            message.setReceiver(receiver);
            message.setContent(webSocketMessage.getContent());
            message.setMediaUrl(webSocketMessage.getMediaUrl());
            message.setMediaType(webSocketMessage.getMediaType());
            
            // Сохраняем сообщение в базе данных
            message = messageService.saveMessage(message);
            
            // Сохраняем в Firebase Realtime Database
            firebaseService.saveMessageToRealtimeDB(message);
            
            // Отправляем в WebSocket по специальному маршруту для получателя
            messagingTemplate.convertAndSend(
                    "/topic/user." + receiver.getId(),
                    message
            );
            
            // Отправляем push-уведомление получателю
            firebaseService.sendNewMessageNotification(message);
        }
    }
    
    /**
     * Обрабатывает обновление статуса сообщения
     */
    @MessageMapping("/chat.updateStatus")
    public void updateMessageStatus(Map<String, Object> statusUpdate, Principal principal) {
        Long messageId = Long.valueOf(statusUpdate.get("messageId").toString());
        String statusStr = statusUpdate.get("status").toString();
        
        Message.MessageStatus status = Message.MessageStatus.valueOf(statusStr);
        
        // Обновляем статус сообщения в базе данных
        messageService.updateMessageStatus(messageId, status);
        
        // Обновляем статус в Firebase
        firebaseService.updateMessageStatus(messageId, status);
        
        // Отправляем обновление отправителю сообщения
        Message message = messageService.findById(messageId);
        if (message != null) {
            messagingTemplate.convertAndSend(
                    "/topic/user." + message.getSender().getId() + ".status",
                    statusUpdate
            );
        }
    }
    
    /**
     * Обрабатывает отправку групповых сообщений
     */
    @MessageMapping("/chat.group.{groupId}")
    @SendTo("/topic/group.{groupId}")
    public Message sendGroupMessage(@DestinationVariable Long groupId, 
                                    WebSocketMessage webSocketMessage,
                                    Principal principal) {
        User sender = userService.findByEmail(principal.getName());
        
        // Создаем групповое сообщение
        Message message = messageService.createGroupMessage(
                sender, 
                groupId, 
                webSocketMessage.getContent(),
                webSocketMessage.getMediaUrl(),
                webSocketMessage.getMediaType()
        );
        
        // Сохраняем в Firebase
        firebaseService.saveMessageToRealtimeDB(message);
        
        // Отправляем push-уведомления участникам группы
        // Это должно быть реализовано в вашем сервисе сообщений
        
        return message; // Будет автоматически отправлено всем подписчикам группы
    }
    
    /**
     * Класс для передачи сообщений через WebSocket
     */
    public static class WebSocketMessage {
        private Long receiverId;
        private String content;
        private String mediaUrl;
        private String mediaType;
        
        public Long getReceiverId() {
            return receiverId;
        }
        
        public void setReceiverId(Long receiverId) {
            this.receiverId = receiverId;
        }
        
        public String getContent() {
            return content;
        }
        
        public void setContent(String content) {
            this.content = content;
        }
        
        public String getMediaUrl() {
            return mediaUrl;
        }
        
        public void setMediaUrl(String mediaUrl) {
            this.mediaUrl = mediaUrl;
        }
        
        public String getMediaType() {
            return mediaType;
        }
        
        public void setMediaType(String mediaType) {
            this.mediaType = mediaType;
        }
    }
} 