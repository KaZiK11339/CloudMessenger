package com.cloudmessenger.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Контроллер для обработки авторизации и регистрации пользователей
 */
@Controller
public class AuthController {
    
    /**
     * Отображение страницы входа/регистрации
     */
    @GetMapping("/login")
    public String showLoginPage(@RequestParam(value = "error", required = false) String error,
                               @RequestParam(value = "logout", required = false) String logout,
                               Model model) {
        
        // Добавляем сообщения об ошибках или выходе
        if (error != null) {
            model.addAttribute("error", "Неверные учетные данные.");
        }
        
        if (logout != null) {
            model.addAttribute("message", "Вы успешно вышли из системы.");
        }
        
        return "login";
    }
    
    /**
     * Перенаправление для регистрации
     */
    @GetMapping("/register")
    public String showRegisterPage() {
        // Перенаправляем на страницу входа с открытой вкладкой регистрации
        return "redirect:/login?register";
    }
} 