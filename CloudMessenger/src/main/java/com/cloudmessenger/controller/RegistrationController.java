package com.cloudmessenger.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cloudmessenger.model.User;
import com.cloudmessenger.service.UserService;

/**
 * Контроллер для обработки регистрации пользователей
 */
@Controller
@RequestMapping("/register")
public class RegistrationController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    /**
     * Обработка формы регистрации
     */
    @PostMapping("/process")
    public String processRegistration(@RequestParam String email,
                                     @RequestParam String password,
                                     @RequestParam String name,
                                     HttpServletRequest request,
                                     RedirectAttributes redirectAttributes) {
        
        // Проверяем, существует ли пользователь с таким email
        if (userService.findByEmail(email) != null) {
            redirectAttributes.addFlashAttribute("error", "Пользователь с таким email уже существует!");
            return "redirect:/login?register";
        }
        
        try {
            // Создаем нового пользователя
            User newUser = userService.createUser(email, password, name);
            
            // Автоматически аутентифицируем пользователя
            authenticateUserAndSetSession(newUser, password, request);
            
            return "redirect:/messenger";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка при регистрации: " + e.getMessage());
            return "redirect:/login?register";
        }
    }
    
    /**
     * Автоматическая аутентификация пользователя после регистрации
     */
    private void authenticateUserAndSetSession(User user, String password, HttpServletRequest request) {
        UsernamePasswordAuthenticationToken token = 
                new UsernamePasswordAuthenticationToken(user.getEmail(), password);
        
        // Для правильной работы Spring Security
        request.getSession();
        token.setDetails(new WebAuthenticationDetails(request));
        
        Authentication authenticatedUser = authenticationManager.authenticate(token);
        SecurityContextHolder.getContext().setAuthentication(authenticatedUser);
    }
} 