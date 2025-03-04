package com.cloudmessenger.controller;

import com.cloudmessenger.model.User;
import com.cloudmessenger.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Контроллер для обработки FCM токенов
 */
@RestController
@RequestMapping("/api/user")
public class FCMController {

    private final UserService userService;
    
    @Autowired
    public FCMController(UserService userService) {
        this.userService = userService;
    }
    
    /**
     * Сохраняет FCM токен пользователя для push-уведомлений
     */
    @PostMapping("/fcm-token")
    public ResponseEntity<?> saveFCMToken(@RequestBody Map<String, String> tokenData, Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        
        // Проверяем, что пользователь аутентифицирован
        if (authentication == null || !authentication.isAuthenticated()) {
            response.put("success", false);
            response.put("message", "Пользователь не аутентифицирован");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        
        // Получаем токен из запроса
        String token = tokenData.get("token");
        if (token == null || token.isEmpty()) {
            response.put("success", false);
            response.put("message", "Токен не предоставлен");
            return ResponseEntity.badRequest().body(response);
        }
        
        try {
            // Получаем текущего пользователя
            String userEmail = authentication.getName();
            User user = userService.findByEmail(userEmail);
            
            if (user == null) {
                response.put("success", false);
                response.put("message", "Пользователь не найден");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            
            // Сохраняем FCM токен для пользователя
            userService.saveFCMToken(user.getId(), token);
            
            response.put("success", true);
            response.put("message", "FCM токен успешно сохранен");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Ошибка при сохранении токена: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
} 