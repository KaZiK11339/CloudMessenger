package com.cloudmessenger.util;

import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

/**
 * Утилитный класс для работы с Hibernate
 */
public class HibernateUtil {
    
    private static StandardServiceRegistry registry;
    private static SessionFactory sessionFactory;
    
    /**
     * Получение фабрики сессий Hibernate
     * @return SessionFactory
     */
    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            try {
                // Создаем реестр сервисов
                registry = new StandardServiceRegistryBuilder()
                        .configure() // использует hibernate.cfg.xml по умолчанию
                        .build();
                
                // Создаем MetadataSources
                MetadataSources sources = new MetadataSources(registry);
                
                // Создаем Metadata
                Metadata metadata = sources.getMetadataBuilder().build();
                
                // Создаем SessionFactory
                sessionFactory = metadata.getSessionFactoryBuilder().build();
                
            } catch (Exception e) {
                if (registry != null) {
                    StandardServiceRegistryBuilder.destroy(registry);
                }
                throw new ExceptionInInitializerError("Ошибка инициализации Hibernate: " + e.getMessage());
            }
        }
        return sessionFactory;
    }
    
    /**
     * Закрытие фабрики сессий Hibernate
     */
    public static void shutdown() {
        if (registry != null) {
            StandardServiceRegistryBuilder.destroy(registry);
        }
    }
} 