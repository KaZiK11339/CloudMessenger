package com.cloudmessenger.service;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudmessenger.model.Group;
import com.cloudmessenger.model.Message;
import com.cloudmessenger.model.Message.MessageType;
import com.cloudmessenger.model.Message.RecipientType;
import com.cloudmessenger.model.User;
import com.cloudmessenger.util.HibernateUtil;

/**
 * Сервис для работы с сообщениями
 */
@Service
public class MessageService {

    private final FileStorageService fileStorageService;
    private final GroupService groupService;

    @Autowired
    public MessageService(FileStorageService fileStorageService, GroupService groupService) {
        this.fileStorageService = fileStorageService;
        this.groupService = groupService;
    }

    /**
     * Создает текстовое сообщение для пользователя
     */
    public Message createTextMessage(User sender, String recipientPhone, String content) {
        Message message = new Message(sender, recipientPhone, content);
        saveMessage(message);
        return message;
    }
    
    /**
     * Создает текстовое сообщение для группы
     */
    public Message createGroupTextMessage(User sender, Group group, String content) {
        // Проверяем, является ли отправитель участником группы
        if (!group.isMember(sender)) {
            throw new IllegalArgumentException("Пользователь не является участником группы");
        }
        
        Message message = new Message(sender, group, content);
        saveMessage(message);
        return message;
    }

    /**
     * Создает медиа-сообщение для пользователя
     */
    public Message createMediaMessage(User sender, String recipientPhone, MultipartFile file) {
        // Проверяем тип файла
        if (!fileStorageService.isAllowedFileType(file.getContentType())) {
            throw new IllegalArgumentException("Недопустимый тип файла: " + file.getContentType());
        }
        
        // Определяем тип сообщения на основе MIME-типа
        MessageType messageType = fileStorageService.determineMessageType(file.getContentType());
        
        // Сохраняем файл и получаем его путь
        String filePath = fileStorageService.storeFile(file);
        
        // Создаем сообщение
        Message message = new Message(
            sender, 
            recipientPhone, 
            messageType, 
            file.getOriginalFilename(), 
            filePath, 
            file.getContentType(), 
            file.getSize()
        );
        
        saveMessage(message);
        return message;
    }
    
    /**
     * Создает медиа-сообщение для группы
     */
    public Message createGroupMediaMessage(User sender, Group group, MultipartFile file) {
        // Проверяем, является ли отправитель участником группы
        if (!group.isMember(sender)) {
            throw new IllegalArgumentException("Пользователь не является участником группы");
        }
        
        // Проверяем тип файла
        if (!fileStorageService.isAllowedFileType(file.getContentType())) {
            throw new IllegalArgumentException("Недопустимый тип файла: " + file.getContentType());
        }
        
        // Определяем тип сообщения на основе MIME-типа
        MessageType messageType = fileStorageService.determineMessageType(file.getContentType());
        
        // Сохраняем файл и получаем его путь
        String filePath = fileStorageService.storeFile(file);
        
        // Создаем сообщение
        Message message = new Message(
            sender, 
            group, 
            messageType, 
            file.getOriginalFilename(), 
            filePath, 
            file.getContentType(), 
            file.getSize()
        );
        
        saveMessage(message);
        return message;
    }

    /**
     * Сохраняет сообщение в базе данных
     */
    private void saveMessage(Message message) {
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();
        Transaction tx = null;
        
        try {
            tx = session.beginTransaction();
            session.save(message);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            throw e;
        } finally {
            session.close();
        }
    }

    /**
     * Получает сообщения между двумя пользователями
     */
    @SuppressWarnings("unchecked")
    public List<Message> getMessagesBetweenUsers(Long userId, String recipientPhone, int limit, int offset) {
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();
        
        try {
            Query<Message> query = session.createQuery(
                "FROM Message m WHERE m.recipientType = :recipientType AND " +
                "((m.sender.id = :userId AND m.recipientPhone = :recipientPhone) OR " +
                "(m.sender.id <> :userId AND m.recipientPhone = :userPhone)) " +
                "ORDER BY m.timestamp DESC");
            
            query.setParameter("recipientType", RecipientType.USER);
            query.setParameter("userId", userId);
            query.setParameter("recipientPhone", recipientPhone);
            query.setParameter("userPhone", getUserPhoneById(session, userId));
            
            query.setMaxResults(limit);
            query.setFirstResult(offset);
            
            return query.list();
        } finally {
            session.close();
        }
    }
    
    /**
     * Получает сообщения в группе
     */
    @SuppressWarnings("unchecked")
    public List<Message> getGroupMessages(Long groupId, int limit, int offset) {
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();
        
        try {
            Query<Message> query = session.createQuery(
                "FROM Message m WHERE m.recipientType = :recipientType AND " +
                "m.group.id = :groupId " +
                "ORDER BY m.timestamp DESC");
            
            query.setParameter("recipientType", RecipientType.GROUP);
            query.setParameter("groupId", groupId);
            
            query.setMaxResults(limit);
            query.setFirstResult(offset);
            
            return query.list();
        } finally {
            session.close();
        }
    }
    
    /**
     * Получает номер телефона пользователя по ID
     */
    private String getUserPhoneById(Session session, Long userId) {
        return (String) session.createQuery("SELECT u.phoneNumber FROM User u WHERE u.id = :userId")
                .setParameter("userId", userId)
                .uniqueResult();
    }

    /**
     * Отмечает сообщение как отправленное
     */
    public void markAsSent(Long messageId) {
        updateMessageStatus(messageId, "sent", true);
    }

    /**
     * Отмечает сообщение как доставленное
     */
    public void markAsDelivered(Long messageId) {
        updateMessageStatus(messageId, "delivered", true);
    }

    /**
     * Обновляет статус сообщения
     */
    private void updateMessageStatus(Long messageId, String field, boolean value) {
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();
        Transaction tx = null;
        
        try {
            tx = session.beginTransaction();
            
            Query<?> query = session.createQuery(
                "UPDATE Message m SET m." + field + " = :value WHERE m.id = :id");
            query.setParameter("value", value);
            query.setParameter("id", messageId);
            query.executeUpdate();
            
            tx.commit();
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            throw e;
        } finally {
            session.close();
        }
    }
    
    /**
     * Обновляет текстовое содержимое сообщения
     */
    public void updateMessageContent(Long messageId, String content) {
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();
        Transaction tx = null;
        
        try {
            tx = session.beginTransaction();
            
            Query<?> query = session.createQuery(
                "UPDATE Message m SET m.content = :content WHERE m.id = :id");
            query.setParameter("content", content);
            query.setParameter("id", messageId);
            query.executeUpdate();
            
            tx.commit();
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            throw e;
        } finally {
            session.close();
        }
    }
    
    /**
     * Получает сообщение по ID
     */
    public Message getMessage(Long messageId) {
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();
        
        try {
            return session.get(Message.class, messageId);
        } finally {
            session.close();
        }
    }

    /**
     * Удаляет сообщение
     * Если сообщение содержит файл, он также удаляется
     */
    public void deleteMessage(Long messageId) {
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();
        Transaction tx = null;
        
        try {
            tx = session.beginTransaction();
            
            Message message = session.get(Message.class, messageId);
            if (message != null) {
                // Если сообщение содержит файл, удаляем его
                if (message.getMessageType() != MessageType.TEXT && message.getFilePath() != null) {
                    fileStorageService.deleteFile(message.getFilePath());
                }
                
                session.delete(message);
            }
            
            tx.commit();
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            throw e;
        } finally {
            session.close();
        }
    }
} 