package com.cloudmessenger.security;

import java.io.IOException;
import java.util.Collection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

/**
 * Обработчик успешной аутентификации, который перенаправляет
 * пользователей на разные страницы в зависимости от их роли
 */
public class CustomAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler 
                                                implements AuthenticationSuccessHandler {
    
    private static final String ADMIN_EMAIL = "admin@cloudmessenger.com";
    private static final String ADMIN_TARGET_URL = "/admin";
    private static final String DEFAULT_TARGET_URL = "/messenger";
    
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                      Authentication authentication) throws IOException, ServletException {
        
        String targetUrl = determineTargetUrl(authentication);
        
        // Перенаправление на соответствующий URL
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
    
    /**
     * Определяет URL для перенаправления на основе роли пользователя
     */
    protected String determineTargetUrl(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();
        
        // Проверяем, является ли пользователь администратором по email
        if (ADMIN_EMAIL.equals(username)) {
            return ADMIN_TARGET_URL;
        }
        
        // Для всех остальных пользователей - стандартный URL
        return DEFAULT_TARGET_URL;
    }
} 