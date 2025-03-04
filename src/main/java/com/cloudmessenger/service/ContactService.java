package com.cloudmessenger.service;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.stereotype.Service;

import com.cloudmessenger.model.Contact;
import com.cloudmessenger.model.User;
import com.cloudmessenger.util.HibernateUtil;

/**
 * Сервис для работы с контактами
 */
@Service
public class ContactService {

    /**
     * Создание нового контакта
     */
    public Contact createContact(User user, String name, String phoneNumber, String email) {
        Contact contact = new Contact();
        contact.setUser(user);
        contact.setName(name);
        contact.setPhoneNumber(phoneNumber);
        contact.setEmail(email);
        
        saveContact(contact);
        
        return contact;
    }
    
    /**
     * Сохранение контакта в базу данных
     */
    private void saveContact(Contact contact) {
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();
        Transaction tx = null;
        
        try {
            tx = session.beginTransaction();
            session.save(contact);
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
     * Получение всех контактов пользователя
     */
    public List<Contact> findByUser(User user) {
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();
        
        try {
            Query<Contact> query = session.createQuery("FROM Contact WHERE user = :user ORDER BY name", Contact.class);
            query.setParameter("user", user);
            return query.list();
        } finally {
            session.close();
        }
    }
    
    /**
     * Поиск контакта по ID
     */
    public Contact findById(Long id) {
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();
        
        try {
            return session.get(Contact.class, id);
        } finally {
            session.close();
        }
    }
    
    /**
     * Поиск контакта по номеру телефона
     */
    public Contact findByPhone(User user, String phoneNumber) {
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();
        
        try {
            Query<Contact> query = session.createQuery(
                "FROM Contact WHERE user = :user AND phoneNumber = :phoneNumber", 
                Contact.class);
            query.setParameter("user", user);
            query.setParameter("phoneNumber", phoneNumber);
            return query.uniqueResult();
        } finally {
            session.close();
        }
    }
    
    /**
     * Обновление контакта
     */
    public void updateContact(Contact contact) {
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();
        Transaction tx = null;
        
        try {
            tx = session.beginTransaction();
            session.update(contact);
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
     * Удаление контакта
     */
    public void deleteContact(Long contactId) {
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();
        Transaction tx = null;
        
        try {
            tx = session.beginTransaction();
            
            Contact contact = session.get(Contact.class, contactId);
            if (contact != null) {
                session.delete(contact);
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
} 