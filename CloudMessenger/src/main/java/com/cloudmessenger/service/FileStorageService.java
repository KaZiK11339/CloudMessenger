package com.cloudmessenger.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.cloudmessenger.config.ConfigProperties;
import com.cloudmessenger.exception.FileStorageException;
import com.cloudmessenger.model.Message.MessageType;

/**
 * Сервис для работы с файлами
 */
@Service
public class FileStorageService {
    
    private static final Logger LOGGER = Logger.getLogger(FileStorageService.class.getName());
    
    private final ConfigProperties configProperties;
    private Path fileStorageLocation;
    
    @Autowired
    public FileStorageService(ConfigProperties configProperties) {
        this.configProperties = configProperties;
    }
    
    /**
     * Инициализация директории для хранения файлов
     */
    @PostConstruct
    public void init() {
        this.fileStorageLocation = Paths.get(configProperties.getUploadDir())
                .toAbsolutePath().normalize();
        
        try {
            Files.createDirectories(this.fileStorageLocation);
            LOGGER.info("Директория для хранения файлов создана: " + this.fileStorageLocation);
        } catch (Exception ex) {
            throw new FileStorageException("Не удалось создать директорию для хранения файлов", ex);
        }
    }
    
    /**
     * Проверка допустимого типа файла
     */
    public boolean isAllowedFileType(String contentType) {
        return Arrays.asList(configProperties.getAllowedFileTypesArray())
                .contains(contentType);
    }
    
    /**
     * Определение типа сообщения на основе MIME-типа
     */
    public MessageType determineMessageType(String mimeType) {
        if (mimeType == null) {
            return MessageType.FILE;
        }
        
        if (mimeType.startsWith("image/")) {
            return MessageType.IMAGE;
        } else if (mimeType.startsWith("video/")) {
            return MessageType.VIDEO;
        } else if (mimeType.startsWith("audio/")) {
            return MessageType.AUDIO;
        } else {
            return MessageType.FILE;
        }
    }
    
    /**
     * Сохранение файла
     */
    public String storeFile(MultipartFile file) {
        // Проверяем допустимый размер файла
        if (file.getSize() > configProperties.getMaxFileSize()) {
            throw new FileStorageException("Размер файла превышает максимально допустимый (" + 
                    configProperties.getMaxFileSize() / (1024 * 1024) + "MB)");
        }
        
        // Проверяем допустимый тип файла
        if (!isAllowedFileType(file.getContentType())) {
            throw new FileStorageException("Тип файла не поддерживается: " + file.getContentType());
        }
        
        // Генерируем уникальное имя файла
        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
        String fileExtension = "";
        if (originalFilename.contains(".")) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        
        String filename = UUID.randomUUID().toString() + fileExtension;
        
        try {
            // Создаем подпапку по типу файла
            MessageType messageType = determineMessageType(file.getContentType());
            String subDir = messageType.name().toLowerCase();
            Path targetDir = this.fileStorageLocation.resolve(subDir);
            Files.createDirectories(targetDir);
            
            // Сохраняем файл
            Path targetLocation = targetDir.resolve(filename);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            
            // Возвращаем относительный путь к файлу
            return subDir + File.separator + filename;
            
        } catch (IOException ex) {
            throw new FileStorageException("Не удалось сохранить файл " + filename, ex);
        }
    }
    
    /**
     * Получение файла
     */
    public Resource loadFileAsResource(String filePath) {
        try {
            Path file = this.fileStorageLocation.resolve(filePath).normalize();
            Resource resource = new UrlResource(file.toUri());
            
            if (resource.exists()) {
                return resource;
            } else {
                throw new FileStorageException("Файл не найден: " + filePath);
            }
        } catch (Exception ex) {
            throw new FileStorageException("Не удалось загрузить файл: " + filePath, ex);
        }
    }
    
    /**
     * Удаление файла
     */
    public boolean deleteFile(String filePath) {
        try {
            Path file = this.fileStorageLocation.resolve(filePath).normalize();
            return Files.deleteIfExists(file);
        } catch (IOException ex) {
            LOGGER.log(Level.WARNING, "Не удалось удалить файл: " + filePath, ex);
            return false;
        }
    }
} 