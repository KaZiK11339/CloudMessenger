package com.cloudmessenger.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cloudmessenger.model.User;
import com.cloudmessenger.service.UserService;

/**
 * Контроллер для административных функций
 */
@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    
    @Autowired
    public AdminController(UserService userService) {
        this.userService = userService;
    }
    
    /**
     * Отображает главную страницу административной панели
     */
    @GetMapping
    public String showAdminDashboard(Model model, Principal principal) {
        // Получаем текущего пользователя (администратора)
        User admin = userService.findByEmail(principal.getName());
        
        // Получаем всех пользователей системы
        List<User> users = userService.findAllUsers();
        
        model.addAttribute("admin", admin);
        model.addAttribute("users", users);
        
        return "admin/dashboard";
    }
    
    /**
     * Блокирует/разблокирует пользователя
     */
    @PostMapping("/users/{userId}/toggle-status")
    public String toggleUserStatus(@PathVariable Long userId, 
                                  RedirectAttributes redirectAttributes) {
        try {
            User user = userService.findById(userId);
            if (user != null) {
                user.setEnabled(!user.isEnabled());
                userService.updateUser(user);
                
                String status = user.isEnabled() ? "разблокирован" : "заблокирован";
                redirectAttributes.addFlashAttribute("successMessage", 
                    "Пользователь " + user.getEmail() + " успешно " + status);
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Пользователь не найден");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Ошибка при изменении статуса пользователя: " + e.getMessage());
        }
        
        return "redirect:/admin";
    }
} 