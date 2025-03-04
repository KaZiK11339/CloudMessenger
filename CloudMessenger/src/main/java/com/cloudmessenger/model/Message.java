package com.cloudmessenger.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * Модель сообщения
 */
@Entity
@Table(name = "messages")
public class Message {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;
    
    @Column(nullable = true)
    private String recipientPhone;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private Group group;
    
    @Column(nullable = true)
    private String content;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date timestamp;
    
    @Column(nullable = false)
    private boolean sent = false;
    
    @Column(nullable = false)
    private boolean delivered = false;
    
    // Добавляем тип сообщения
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MessageType messageType = MessageType.TEXT;
    
    // Поле для хранения имени файла
    @Column
    private String fileName;
    
    // Поле для хранения пути к файлу
    @Column
    private String filePath;
    
    // Поле для хранения типа MIME
    @Column
    private String mimeType;
    
    // Поле для хранения размера файла в байтах
    @Column
    private Long fileSize;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RecipientType recipientType = RecipientType.USER;
    
    // Перечисление типов сообщений
    public enum MessageType {
        TEXT,        // Текстовое сообщение
        IMAGE,       // Изображение
        VIDEO,       // Видео
        AUDIO,       // Аудио
        FILE         // Другой файл
    }
    
    public enum RecipientType {
        USER,        // Сообщение пользователю
        GROUP        // Сообщение в группу
    }
    
    // Конструкторы
    public Message() {
        this.timestamp = new Date();
    }
    
    // Конструктор для личных сообщений
    public Message(User sender, String recipientPhone, String content) {
        this.sender = sender;
        this.recipientPhone = recipientPhone;
        this.content = content;
        this.timestamp = new Date();
        this.messageType = MessageType.TEXT;
        this.recipientType = RecipientType.USER;
    }
    
    // Конструктор для групповых сообщений
    public Message(User sender, Group group, String content) {
        this.sender = sender;
        this.group = group;
        this.content = content;
        this.timestamp = new Date();
        this.messageType = MessageType.TEXT;
        this.recipientType = RecipientType.GROUP;
    }
    
    // Конструктор для медиафайлов в личных сообщениях
    public Message(User sender, String recipientPhone, MessageType messageType, 
                   String fileName, String filePath, String mimeType, Long fileSize) {
        this.sender = sender;
        this.recipientPhone = recipientPhone;
        this.messageType = messageType;
        this.fileName = fileName;
        this.filePath = filePath;
        this.mimeType = mimeType;
        this.fileSize = fileSize;
        this.timestamp = new Date();
        this.recipientType = RecipientType.USER;
    }
    
    // Конструктор для медиафайлов в групповых сообщениях
    public Message(User sender, Group group, MessageType messageType, 
                   String fileName, String filePath, String mimeType, Long fileSize) {
        this.sender = sender;
        this.group = group;
        this.messageType = messageType;
        this.fileName = fileName;
        this.filePath = filePath;
        this.mimeType = mimeType;
        this.fileSize = fileSize;
        this.timestamp = new Date();
        this.recipientType = RecipientType.GROUP;
    }
    
    // Геттеры и сеттеры
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public User getSender() {
        return sender;
    }
    
    public void setSender(User sender) {
        this.sender = sender;
    }
    
    public String getRecipientPhone() {
        return recipientPhone;
    }
    
    public void setRecipientPhone(String recipientPhone) {
        this.recipientPhone = recipientPhone;
    }
    
    public Group getGroup() {
        return group;
    }
    
    public void setGroup(Group group) {
        this.group = group;
        if (group != null) {
            this.recipientType = RecipientType.GROUP;
        }
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public Date getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
    
    public boolean isSent() {
        return sent;
    }
    
    public void setSent(boolean sent) {
        this.sent = sent;
    }
    
    public boolean isDelivered() {
        return delivered;
    }
    
    public void setDelivered(boolean delivered) {
        this.delivered = delivered;
    }
    
    public MessageType getMessageType() {
        return messageType;
    }
    
    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }
    
    public String getFileName() {
        return fileName;
    }
    
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    
    public String getFilePath() {
        return filePath;
    }
    
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
    
    public String getMimeType() {
        return mimeType;
    }
    
    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }
    
    public Long getFileSize() {
        return fileSize;
    }
    
    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }
    
    public RecipientType getRecipientType() {
        return recipientType;
    }
    
    public void setRecipientType(RecipientType recipientType) {
        this.recipientType = recipientType;
    }
    
    public boolean isGroupMessage() {
        return recipientType == RecipientType.GROUP;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        Message message = (Message) o;
        
        return id != null ? id.equals(message.id) : message.id == null;
    }
    
    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
} 