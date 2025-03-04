package com.cloudmessenger.model;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * Модель пользователя системы
 */
@Entity
@Table(name = "users")
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String email;
    
    @Column(nullable = false)
    private String password;
    
    @Column(nullable = false)
    private String name;
    
    @Column
    private String phoneNumber;
    
    @Column(nullable = false)
    private boolean enabled = true;
    
    @Column(nullable = false)
    private boolean blocked = false;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "registration_date")
    private Date registrationDate;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "last_login_date")
    private Date lastLoginDate;
    
    @Column(name = "fcm_token")
    private String fcmToken;
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Contact> contacts = new HashSet<>();
    
    // Конструкторы
    public User() {
        this.registrationDate = new Date();
        this.lastLoginDate = new Date();
    }
    
    public User(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.registrationDate = new Date();
        this.lastLoginDate = new Date();
    }
    
    // Геттеры и сеттеры
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    public boolean isBlocked() {
        return blocked;
    }
    
    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }
    
    public Date getRegistrationDate() {
        return registrationDate;
    }
    
    public void setRegistrationDate(Date registrationDate) {
        this.registrationDate = registrationDate;
    }
    
    public Date getLastLoginDate() {
        return lastLoginDate;
    }
    
    public void setLastLoginDate(Date lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }
    
    public String getFcmToken() {
        return fcmToken;
    }
    
    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }
    
    public Set<Contact> getContacts() {
        return contacts;
    }
    
    public void setContacts(Set<Contact> contacts) {
        this.contacts = contacts;
    }
    
    public void addContact(Contact contact) {
        contacts.add(contact);
        contact.setUser(this);
    }
    
    public void removeContact(Contact contact) {
        contacts.remove(contact);
        contact.setUser(null);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        User user = (User) o;
        return id != null && id.equals(user.getId());
    }
    
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
} 