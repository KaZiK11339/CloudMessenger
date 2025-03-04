package com.cloudmessenger.security;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.cloudmessenger.model.User;
import com.cloudmessenger.service.UserService;

/**
 * Провайдер аутентификации, который позволяет входить как по email, так и по ID пользователя
 */
@Component
public class FlexibleAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String loginInput = authentication.getName();
        String password = authentication.getCredentials().toString();
        
        User user = null;
        
        // Пытаемся найти пользователя по email
        user = userService.findByEmail(loginInput);
        
        // Если пользователь не найден по email, пробуем найти по ID
        if (user == null) {
            try {
                Long userId = Long.parseLong(loginInput);
                user = userService.findById(userId);
            } catch (NumberFormatException e) {
                // Если не удалось преобразовать к Long, значит это точно не ID
                throw new BadCredentialsException("Неверные учетные данные");
            }
        }
        
        // Проверяем найден ли пользователь и активен ли он
        if (user == null || !user.isEnabled()) {
            throw new BadCredentialsException("Неверные учетные данные или аккаунт отключен");
        }
        
        // Проверяем пароль
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BadCredentialsException("Неверный пароль");
        }
        
        // Определяем роли пользователя
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        
        // Если это администратор, добавляем роль ADMIN
        if ("admin@cloudmessenger.com".equals(user.getEmail())) {
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        }
        
        // Создаем и возвращаем аутентификационный токен
        return new UsernamePasswordAuthenticationToken(user.getEmail(), password, authorities);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
} 