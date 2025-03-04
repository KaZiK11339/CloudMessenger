package com.cloudmessenger.util;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Инициализатор базы данных SQLite
 * Создает базу данных и необходимые таблицы при запуске приложения.
 */
public class DatabaseInitializer implements ServletContextListener {

    private static final Logger LOGGER = Logger.getLogger(DatabaseInitializer.class.getName());

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        String catalinaHome = System.getProperty("catalina.home");
        String dbPath = catalinaHome + File.separator + "cloudmessenger.db";
        
        File dbFile = new File(dbPath);
        boolean dbExists = dbFile.exists();
        
        if (!dbExists) {
            LOGGER.info("База данных не найдена. Создание новой базы данных SQLite...");
            
            try {
                // Загружаем драйвер SQLite
                Class.forName("org.sqlite.JDBC");
                
                // Устанавливаем соединение с базой данных
                try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
                     Statement stmt = conn.createStatement()) {
                    
                    // Создаем таблицы базы данных
                    createTables(stmt);
                    
                    LOGGER.info("База данных и таблицы успешно созданы.");
                }
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Ошибка при инициализации базы данных", e);
            }
        }
    }
    
    private void createTables(Statement stmt) throws Exception {
        // Создаем таблицу пользователей
        stmt.execute("CREATE TABLE IF NOT EXISTS users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "email VARCHAR(255) NOT NULL UNIQUE, " +
                "password VARCHAR(255) NOT NULL, " +
                "name VARCHAR(100) NOT NULL, " +
                "phone_number VARCHAR(20), " +
                "avatar_path VARCHAR(255), " +
                "created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP" +
                ")");
        
        // Создаем таблицу контактов
        stmt.execute("CREATE TABLE IF NOT EXISTS contacts (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER NOT NULL, " +
                "name VARCHAR(100) NOT NULL, " +
                "phone_number VARCHAR(20) NOT NULL, " +
                "email VARCHAR(255), " +
                "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE" +
                ")");
        
        // Создаем таблицу групп
        stmt.execute("CREATE TABLE IF NOT EXISTS groups (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name VARCHAR(100) NOT NULL, " +
                "description TEXT, " +
                "avatar_path VARCHAR(255), " +
                "creator_id INTEGER NOT NULL, " +
                "created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY (creator_id) REFERENCES users(id) ON DELETE CASCADE" +
                ")");
        
        // Создаем связующую таблицу участников групп
        stmt.execute("CREATE TABLE IF NOT EXISTS group_members (" +
                "group_id INTEGER NOT NULL, " +
                "user_id INTEGER NOT NULL, " +
                "joined_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                "PRIMARY KEY (group_id, user_id), " +
                "FOREIGN KEY (group_id) REFERENCES groups(id) ON DELETE CASCADE, " +
                "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE" +
                ")");
        
        // Создаем таблицу сообщений с поддержкой медиафайлов и групповых чатов
        stmt.execute("CREATE TABLE IF NOT EXISTS messages (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "sender_id INTEGER NOT NULL, " +
                "recipient_phone VARCHAR(20), " +
                "group_id INTEGER, " +
                "content TEXT, " +
                "timestamp TIMESTAMP NOT NULL, " +
                "sent BOOLEAN NOT NULL DEFAULT 0, " +
                "delivered BOOLEAN NOT NULL DEFAULT 0, " +
                "message_type TEXT NOT NULL DEFAULT 'TEXT', " +
                "recipient_type TEXT NOT NULL DEFAULT 'USER', " +
                "file_name TEXT, " +
                "file_path TEXT, " +
                "mime_type TEXT, " +
                "file_size INTEGER, " +
                "FOREIGN KEY (sender_id) REFERENCES users(id) ON DELETE CASCADE, " +
                "FOREIGN KEY (group_id) REFERENCES groups(id) ON DELETE CASCADE" +
                ")");
        
        // Создаем индексы для оптимизации запросов
        stmt.execute("CREATE INDEX IF NOT EXISTS idx_messages_sender ON messages(sender_id)");
        stmt.execute("CREATE INDEX IF NOT EXISTS idx_messages_recipient_phone ON messages(recipient_phone)");
        stmt.execute("CREATE INDEX IF NOT EXISTS idx_messages_group ON messages(group_id)");
        stmt.execute("CREATE INDEX IF NOT EXISTS idx_group_members_group ON group_members(group_id)");
        stmt.execute("CREATE INDEX IF NOT EXISTS idx_group_members_user ON group_members(user_id)");
        stmt.execute("CREATE INDEX IF NOT EXISTS idx_contacts_user ON contacts(user_id)");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // Ничего не делаем при завершении работы приложения
    }
} 