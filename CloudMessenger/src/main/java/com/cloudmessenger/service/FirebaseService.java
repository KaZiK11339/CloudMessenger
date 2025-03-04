package com.cloudmessenger.service;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cloudmessenger.model.Message.MessageStatus;
import com.cloudmessenger.model.User;
import com.cloudmessenger.model.Message;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Сервис для работы с Firebase Realtime Database и Firebase Cloud Messaging
 */
@Service
public class FirebaseService {

    private final FirebaseDatabase firebaseDatabase;
    
    @Autowired
    public FirebaseService(FirebaseDatabase firebaseDatabase) {
        this.firebaseDatabase = firebaseDatabase;
    }
    
    /**
     * Сохраняет сообщение в Realtime Database для синхронизации в реальном времени
     */
    public void saveMessageToRealtimeDB(Message message) {
        DatabaseReference messagesRef = firebaseDatabase.getReference("messages");
        
        // Подготавливаем данные для сохранения
        Map<String, Object> messageData = new HashMap<>();
        messageData.put("id", message.getId());
        messageData.put("senderId", message.getSender().getId());
        messageData.put("receiverId", message.getReceiver().getId());
        messageData.put("content", message.getContent());
        messageData.put("mediaUrl", message.getMediaUrl());
        messageData.put("mediaType", message.getMediaType());
        messageData.put("timestamp", message.getTimestamp().getTime());
        messageData.put("status", message.getStatus().toString());
        
        // Если это групповое сообщение, добавляем идентификатор группы
        if (message.getGroup() != null) {
            messageData.put("groupId", message.getGroup().getId());
        }
        
        // Сохраняем сообщение с уникальным идентификатором
        messagesRef.child(message.getId().toString()).setValueAsync(messageData);
    }
    
    /**
     * Обновляет статус сообщения в Realtime Database
     */
    public void updateMessageStatus(Long messageId, MessageStatus status) {
        DatabaseReference messageRef = firebaseDatabase.getReference("messages")
                .child(messageId.toString());
        
        // Обновляем только поле статуса
        Map<String, Object> updates = new HashMap<>();
        updates.put("status", status.toString());
        
        messageRef.updateChildrenAsync(updates);
    }
    
    /**
     * Отправляет push-уведомление пользователю
     */
    public void sendPushNotification(User recipient, String title, String body, Map<String, String> data) {
        // Получаем токен FCM из базы данных пользователя
        // В реальном приложении требуется хранить FCM токены пользователей
        String fcmToken = getFCMTokenForUser(recipient);
        
        if (fcmToken == null || fcmToken.isEmpty()) {
            return; // Отсутствует токен FCM - пропускаем отправку
        }
        
        try {
            // Создаем уведомление
            Notification notification = Notification.builder()
                    .setTitle(title)
                    .setBody(body)
                    .build();
            
            // Создаем сообщение
            Message.Builder messageBuilder = Message.builder()
                    .setToken(fcmToken)
                    .setNotification(notification);
            
            // Добавляем дополнительные данные, если они есть
            if (data != null && !data.isEmpty()) {
                messageBuilder.putAllData(data);
            }
            
            // Отправляем push-уведомление
            String response = FirebaseMessaging.getInstance().send(messageBuilder.build());
            
        } catch (Exception e) {
            // Обработка ошибки отправки уведомления
            System.err.println("Ошибка отправки push-уведомления: " + e.getMessage());
        }
    }
    
    /**
     * Отправляет push-уведомление о новом сообщении
     */
    public void sendNewMessageNotification(Message message) {
        User sender = message.getSender();
        User recipient = message.getReceiver();
        
        String title = sender.getName();
        String body = message.getMediaUrl() != null 
                ? "Отправил(а) медиафайл" 
                : message.getContent();
        
        Map<String, String> data = new HashMap<>();
        data.put("messageId", message.getId().toString());
        data.put("senderId", sender.getId().toString());
        
        // Если это групповое сообщение, добавляем информацию о группе
        if (message.getGroup() != null) {
            title = message.getGroup().getName();
            data.put("groupId", message.getGroup().getId().toString());
        }
        
        sendPushNotification(recipient, title, body, data);
    }
    
    /**
     * Получает FCM токен для пользователя
     * В реальном приложении этот метод должен получать токен из базы данных
     */
    private String getFCMTokenForUser(User user) {
        // TODO: Реализовать получение FCM токена из базы данных
        // Здесь должна быть логика получения токена для пользователя
        return null;
    }
} 