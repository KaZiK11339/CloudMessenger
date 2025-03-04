package com.cloudmessenger.controller;

import java.io.IOException;
import java.security.Principal;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.cloudmessenger.model.Message;
import com.cloudmessenger.model.User;
import com.cloudmessenger.service.FileStorageService;
import com.cloudmessenger.service.MessageService;
import com.cloudmessenger.service.UserService;

/**
 * Контроллер для загрузки и отображения файлов
 */
@Controller
@RequestMapping("/files")
public class FileController {

    private final FileStorageService fileStorageService;
    private final MessageService messageService;
    private final UserService userService;
    
    @Autowired
    public FileController(FileStorageService fileStorageService, 
                         MessageService messageService,
                         UserService userService) {
        this.fileStorageService = fileStorageService;
        this.messageService = messageService;
        this.userService = userService;
    }
    
    /**
     * Метод для загрузки файла и создания сообщения
     */
    @PostMapping("/upload")
    @ResponseBody
    public Message uploadFile(@RequestParam("file") MultipartFile file,
                             @RequestParam("recipientPhone") String recipientPhone,
                             Principal principal) {
        
        // Получаем текущего пользователя
        User currentUser = userService.findByEmail(principal.getName());
        
        // Создаем сообщение с медиафайлом
        return messageService.createMediaMessage(currentUser, recipientPhone, file);
    }
    
    /**
     * Метод для загрузки файла (REST API для мобильных клиентов)
     */
    @PostMapping("/api/upload")
    @ResponseBody
    public Message apiUploadFile(@RequestParam("file") MultipartFile file,
                                @RequestParam("recipientPhone") String recipientPhone,
                                @RequestParam("senderEmail") String senderEmail,
                                @RequestParam("apiKey") String apiKey) {
        
        // Проверяем API ключ (в реальном приложении нужна более надежная аутентификация)
        // TODO: реализовать проверку API ключа
        
        // Получаем пользователя
        User user = userService.findByEmail(senderEmail);
        if (user == null) {
            throw new RuntimeException("Пользователь не найден");
        }
        
        // Создаем сообщение с медиафайлом
        return messageService.createMediaMessage(user, recipientPhone, file);
    }
    
    /**
     * Метод для скачивания/просмотра файла
     */
    @GetMapping("/download/{messageId}")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long messageId,
                                               HttpServletRequest request) throws IOException {
        // Получаем сообщение из базы данных
        // Здесь должна быть безопасная проверка доступа к сообщению
        // TODO: реализовать проверку прав доступа к сообщению
        
        // Получаем путь к файлу из сообщения
        // Для простоты предполагаем, что у нас есть метод получения сообщения по ID
        // TODO: реализовать получение сообщения из базы данных
        String filePath = "path/to/file"; // Заглушка
        
        // Загружаем файл как ресурс
        Resource resource = fileStorageService.loadFileAsResource(filePath);
        
        // Определяем MIME-тип
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            // Лог ошибки определения типа
        }
        
        // Если тип не удалось определить, ставим по умолчанию
        if (contentType == null) {
            contentType = "application/octet-stream";
        }
        
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
    
    /**
     * Метод для просмотра изображений в браузере
     */
    @GetMapping("/view/{messageId}")
    public ResponseEntity<Resource> viewFile(@PathVariable Long messageId,
                                           HttpServletRequest request) throws IOException {
        // Получаем сообщение из базы данных
        // Здесь должна быть безопасная проверка доступа к сообщению
        // TODO: реализовать проверку прав доступа к сообщению
        
        // Получаем путь к файлу из сообщения
        // Для простоты предполагаем, что у нас есть метод получения сообщения по ID
        // TODO: реализовать получение сообщения из базы данных
        String filePath = "path/to/file"; // Заглушка
        
        // Загружаем файл как ресурс
        Resource resource = fileStorageService.loadFileAsResource(filePath);
        
        // Определяем MIME-тип
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            // Лог ошибки определения типа
        }
        
        // Если тип не удалось определить, ставим по умолчанию
        if (contentType == null) {
            contentType = "application/octet-stream";
        }
        
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(resource);
    }
} 