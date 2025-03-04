package com.cloudmessenger.service;

import java.util.Date;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.cloudmessenger.model.User;
import com.cloudmessenger.util.HibernateUtil;

/**
 * Сервис для работы с пользователями
 */
@Service
public class UserService {

    private final PasswordEncoder passwordEncoder;
    
    @Autowired
    public UserService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }
    
    /**
     * Создание нового пользователя
     */
    public User createUser(String name, String email, String password) {
        // Проверяем, существует ли пользователь с таким email
        User existingUser = findByEmail(email);
        if (existingUser != null) {
            // Пользователь с таким email уже существует
            return null;
        }
        
        // Создаем нового пользователя
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);
        user.setRegistrationDate(new Date());
        user.setLastLoginDate(new Date());
        user.setBlocked(false);
        
        // Сохраняем пользователя
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();
        Transaction tx = null;
        
        try {
            tx = session.beginTransaction();
            session.save(user);
            tx.commit();
            return user;
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
     * Сохранение пользователя в базу данных
     */
    private void saveUser(User user) {
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();
        Transaction tx = null;
        
        try {
            tx = session.beginTransaction();
            session.save(user);
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
     * Поиск пользователя по email
     */
    public User findByEmail(String email) {
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();
        
        try {
            Query<User> query = session.createQuery("FROM User WHERE email = :email", User.class);
            query.setParameter("email", email);
            return query.uniqueResult();
        } finally {
            session.close();
        }
    }
    
    /**
     * Поиск пользователя по ID
     */
    public User findById(Long id) {
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();
        
        try {
            return session.get(User.class, id);
        } finally {
            session.close();
        }
    }
    
    /**
     * Получение всех пользователей системы
     */
    public List<User> findAllUsers() {
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();
        
        try {
            return session.createQuery("FROM User", User.class).list();
        } finally {
            session.close();
        }
    }
    
    /**
     * Обновление данных пользователя
     */
    public void updateUser(User user) {
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();
        Transaction tx = null;
        
        try {
            tx = session.beginTransaction();
            session.update(user);
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
     * Обновление пароля пользователя
     */
    public void updatePassword(Long userId, String newPassword) {
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();
        Transaction tx = null;
        
        try {
            tx = session.beginTransaction();
            
            User user = session.get(User.class, userId);
            if (user != null) {
                user.setPassword(passwordEncoder.encode(newPassword));
                session.update(user);
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
    
    /**
     * Удаление пользователя
     */
    public void deleteUser(Long userId) {
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();
        Transaction tx = null;
        
        try {
            tx = session.beginTransaction();
            
            User user = session.get(User.class, userId);
            if (user != null) {
                session.delete(user);
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
    
    /**
     * Проверка пароля пользователя
     */
    public boolean checkPassword(User user, String password) {
        return passwordEncoder.matches(password, user.getPassword());
    }
    
    /**
     * Проверка пользователя
     */
    public User checkUser(String login, String password) {
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();
        
        try {
            // Ищем пользователя по email или ID
            User user;
            
            // Пробуем получить пользователя по email
            Query<User> emailQuery = session.createQuery("FROM User WHERE email = :login", User.class);
            emailQuery.setParameter("login", login);
            user = emailQuery.uniqueResult();
            
            // Если не найден по email, проверяем по ID
            if (user == null) {
                try {
                    Long userId = Long.parseLong(login);
                    user = session.get(User.class, userId);
                } catch (NumberFormatException e) {
                    // Если login не является числом, пользователь не найден
                    return null;
                }
            }
            
            // Проверяем пароль и статус аккаунта
            if (user != null && user.getPassword().equals(password) && !user.isBlocked()) {
                // Обновляем дату последнего входа
                Transaction tx = session.beginTransaction();
                user.setLastLoginDate(new Date());
                session.update(user);
                tx.commit();
                
                return user;
            }
            
            return null;
        } finally {
            session.close();
        }
    }
    
    /**
     * Изменение статуса блокировки пользователя
     */
    public boolean toggleUserBlock(Long userId) {
        User user = findById(userId);
        if (user == null) {
            return false;
        }
        
        // Меняем статус блокировки на противоположный
        user.setBlocked(!user.isBlocked());
        
        // Сохраняем изменения
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();
        Transaction tx = null;
        
        try {
            tx = session.beginTransaction();
            session.update(user);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            return false;
        } finally {
            session.close();
        }
    }
    
    /**
     * Сохраняет FCM токен для пользователя
     * @param userId ID пользователя
     * @param token FCM токен для push-уведомлений
     * @return true если токен успешно сохранен, иначе false
     */
    public boolean saveFCMToken(Long userId, String token) {
        User user = findById(userId);
        if (user == null) {
            return false;
        }
        
        // Сохраняем FCM токен
        user.setFcmToken(token);
        
        // Сохраняем изменения в базе данных
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();
        Transaction tx = null;
        
        try {
            tx = session.beginTransaction();
            session.update(user);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            return false;
        } finally {
            session.close();
        }
    }
} 