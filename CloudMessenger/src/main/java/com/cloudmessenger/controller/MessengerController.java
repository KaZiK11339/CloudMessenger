package com.cloudmessenger.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.cloudmessenger.model.Contact;
import com.cloudmessenger.model.Message;
import com.cloudmessenger.model.User;
import com.cloudmessenger.service.ContactService;
import com.cloudmessenger.service.MessageService;
import com.cloudmessenger.service.UserService;

/**
 * Контроллер для работы с мессенджером
 */
@Controller
public class MessengerController {

    private final MessageService messageService;
    private final UserService userService;
    private final ContactService contactService;
    
    @Autowired
    public MessengerController(MessageService messageService, 
                             UserService userService,
                             ContactService contactService) {
        this.messageService = messageService;
        this.userService = userService;
        this.contactService = contactService;
    }
    
    /**
     * Отображение страницы мессенджера
     */
    @GetMapping("/messenger")
    public String showMessenger(Model model, Principal principal) {
        // Получаем текущего пользователя
        User currentUser = userService.findByEmail(principal.getName());
        
        // Получаем контакты пользователя
        List<Contact> contacts = contactService.findByUser(currentUser);
        
        model.addAttribute("user", currentUser);
        model.addAttribute("contacts", contacts);
        
        return "messenger";
    }
    
    /**
     * Получение сообщений для определенного контакта
     */
    @GetMapping("/messages/{recipientPhone}")
    @ResponseBody
    public List<Message> getMessages(@PathVariable String recipientPhone, 
                                    @RequestParam(defaultValue = "20") int limit,
                                    @RequestParam(defaultValue = "0") int offset,
                                    Principal principal) {
        // Получаем текущего пользователя
        User currentUser = userService.findByEmail(principal.getName());
        
        // Получаем сообщения между пользователями
        return messageService.getMessagesBetweenUsers(currentUser.getId(), recipientPhone, limit, offset);
    }
    
    /**
     * Отправка текстового сообщения
     */
    @PostMapping("/send/text")
    @ResponseBody
    public Message sendTextMessage(@RequestParam String recipientPhone,
                                 @RequestParam String content,
                                 Principal principal) {
        // Получаем текущего пользователя
        User currentUser = userService.findByEmail(principal.getName());
        
        // Создаем и сохраняем сообщение
        return messageService.createTextMessage(currentUser, recipientPhone, content);
    }
    
    /**
     * Отправка сообщения с медиафайлом
     */
    @PostMapping("/send/media")
    @ResponseBody
    public Message sendMediaMessage(@RequestParam("file") MultipartFile file,
                                  @RequestParam String recipientPhone,
                                  @RequestParam(required = false) String text,
                                  Principal principal) {
        // Получаем текущего пользователя
        User currentUser = userService.findByEmail(principal.getName());
        
        // Создаем и сохраняем сообщение с медиафайлом
        Message message = messageService.createMediaMessage(currentUser, recipientPhone, file);
        
        // Если есть текст, добавляем его к сообщению
        if (text != null && !text.isEmpty()) {
            message.setContent(text);
            // Обновляем сообщение в базе данных
            // Здесь должен быть метод для обновления сообщения
        }
        
        return message;
    }
    
    /**
     * Отметка сообщения как доставленное
     */
    @PostMapping("/message/{messageId}/delivered")
    @ResponseBody
    public void markAsDelivered(@PathVariable Long messageId) {
        messageService.markAsDelivered(messageId);
    }
    
    /**
     * Удаление сообщения
     */
    @PostMapping("/message/{messageId}/delete")
    @ResponseBody
    public void deleteMessage(@PathVariable Long messageId, Principal principal) {
        // Проверка, что пользователь имеет право удалить сообщение
        // В реальном приложении нужно добавить проверку
        
        messageService.deleteMessage(messageId);
    }
}