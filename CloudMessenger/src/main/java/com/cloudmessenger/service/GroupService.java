package com.cloudmessenger.service;

import java.util.List;
import java.util.Set;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cloudmessenger.model.Group;
import com.cloudmessenger.model.User;
import com.cloudmessenger.util.HibernateUtil;

/**
 * Сервис для управления групповыми чатами
 */
@Service
public class GroupService {

    @Autowired
    private UserService userService;
    
    /**
     * Создает новую группу
     * 
     * @param name Название группы
     * @param description Описание группы
     * @param creator Создатель группы
     * @return Созданная группа
     */
    public Group createGroup(String name, String description, User creator) {
        Group group = new Group(name, creator);
        group.setDescription(description);
        
        saveGroup(group);
        return group;
    }
    
    /**
     * Сохраняет группу в базе данных
     * 
     * @param group Группа для сохранения
     */
    private void saveGroup(Group group) {
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();
        Transaction tx = null;
        
        try {
            tx = session.beginTransaction();
            session.saveOrUpdate(group);
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
     * Получает группу по идентификатору
     * 
     * @param groupId Идентификатор группы
     * @return Группа или null, если не найдена
     */
    public Group findById(Long groupId) {
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();
        
        try {
            Group group = session.get(Group.class, groupId);
            return group;
        } finally {
            session.close();
        }
    }
    
    /**
     * Получает список всех групп
     * 
     * @return Список всех групп
     */
    @SuppressWarnings("unchecked")
    public List<Group> findAll() {
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();
        
        try {
            Query<Group> query = session.createQuery("FROM Group g ORDER BY g.name");
            return query.list();
        } finally {
            session.close();
        }
    }
    
    /**
     * Получает список групп, в которых пользователь является участником
     * 
     * @param userId Идентификатор пользователя
     * @return Список групп
     */
    @SuppressWarnings("unchecked")
    public List<Group> findByMember(Long userId) {
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();
        
        try {
            Query<Group> query = session.createQuery(
                "SELECT g FROM Group g JOIN g.members m WHERE m.id = :userId ORDER BY g.name");
            query.setParameter("userId", userId);
            return query.list();
        } finally {
            session.close();
        }
    }
    
    /**
     * Получает список групп, созданных пользователем
     * 
     * @param userId Идентификатор пользователя
     * @return Список групп
     */
    @SuppressWarnings("unchecked")
    public List<Group> findByCreator(Long userId) {
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();
        
        try {
            Query<Group> query = session.createQuery(
                "FROM Group g WHERE g.creator.id = :userId ORDER BY g.name");
            query.setParameter("userId", userId);
            return query.list();
        } finally {
            session.close();
        }
    }
    
    /**
     * Обновляет информацию о группе
     * 
     * @param group Группа с обновленными данными
     */
    public void updateGroup(Group group) {
        saveGroup(group);
    }
    
    /**
     * Добавляет пользователя в группу
     * 
     * @param groupId Идентификатор группы
     * @param userId Идентификатор пользователя
     * @return true, если пользователь успешно добавлен
     */
    public boolean addMember(Long groupId, Long userId) {
        Group group = findById(groupId);
        User user = userService.findById(userId);
        
        if (group == null || user == null) {
            return false;
        }
        
        // Проверяем, не является ли пользователь уже участником группы
        if (group.isMember(user)) {
            return true; // Пользователь уже в группе
        }
        
        group.addMember(user);
        saveGroup(group);
        return true;
    }
    
    /**
     * Удаляет пользователя из группы
     * 
     * @param groupId Идентификатор группы
     * @param userId Идентификатор пользователя
     * @return true, если пользователь успешно удален
     */
    public boolean removeMember(Long groupId, Long userId) {
        Group group = findById(groupId);
        User user = userService.findById(userId);
        
        if (group == null || user == null) {
            return false;
        }
        
        // Нельзя удалить создателя группы
        if (group.getCreator().equals(user)) {
            return false;
        }
        
        // Проверяем, является ли пользователь участником группы
        if (!group.isMember(user)) {
            return true; // Пользователь и так не в группе
        }
        
        group.removeMember(user);
        saveGroup(group);
        return true;
    }
    
    /**
     * Получает список участников группы
     * 
     * @param groupId Идентификатор группы
     * @return Список участников или null, если группа не найдена
     */
    public Set<User> getMembers(Long groupId) {
        Group group = findById(groupId);
        if (group == null) {
            return null;
        }
        return group.getMembers();
    }
    
    /**
     * Удаляет группу
     * 
     * @param groupId Идентификатор группы
     */
    public void deleteGroup(Long groupId) {
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();
        Transaction tx = null;
        
        try {
            tx = session.beginTransaction();
            Group group = session.get(Group.class, groupId);
            if (group != null) {
                session.delete(group);
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
     * Проверяет, является ли пользователь участником группы
     * 
     * @param groupId Идентификатор группы
     * @param userId Идентификатор пользователя
     * @return true, если пользователь является участником группы
     */
    public boolean isMember(Long groupId, Long userId) {
        Group group = findById(groupId);
        User user = userService.findById(userId);
        
        if (group == null || user == null) {
            return false;
        }
        
        return group.isMember(user);
    }
    
    /**
     * Проверяет, является ли пользователь создателем группы
     * 
     * @param groupId Идентификатор группы
     * @param userId Идентификатор пользователя
     * @return true, если пользователь является создателем группы
     */
    public boolean isCreator(Long groupId, Long userId) {
        Group group = findById(groupId);
        User user = userService.findById(userId);
        
        if (group == null || user == null) {
            return false;
        }
        
        return group.getCreator().getId().equals(userId);
    }
} 