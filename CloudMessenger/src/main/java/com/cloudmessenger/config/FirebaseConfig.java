package com.cloudmessenger.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.FirebaseDatabase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;

/**
 * Конфигурация Firebase для CloudMessenger
 */
@Configuration
public class FirebaseConfig {
    
    @Value("${firebase.database.url}")
    private String databaseUrl;
    
    @Value("${firebase.config.path}")
    private String configPath;
    
    @PostConstruct
    public void initialize() {
        try {
            // Загружаем файл конфигурации Firebase (JSON) из ресурсов
            ClassPathResource resource = new ClassPathResource(configPath);
            InputStream serviceAccount = resource.getInputStream();
            
            // Создаем опции Firebase с учетными данными и URL базы данных
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setDatabaseUrl(databaseUrl)
                    .build();
            
            // Инициализируем Firebase, если еще не инициализирован
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
            }
            
        } catch (IOException e) {
            throw new RuntimeException("Ошибка инициализации Firebase: " + e.getMessage(), e);
        }
    }
    
    @Bean
    public FirebaseDatabase firebaseDatabase() {
        return FirebaseDatabase.getInstance();
    }
} 