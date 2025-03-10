package com.cloudmessenger.config;

/**
 * Класс конфигурации для Twilio API
 */
public class TwilioConfig {
    
    private String accountSid;
    private String authToken;
    private String phoneNumber;
    
    public String getAccountSid() {
        return accountSid;
    }
    
    public void setAccountSid(String accountSid) {
        this.accountSid = accountSid;
    }
    
    public String getAuthToken() {
        return authToken;
    }
    
    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }
    
    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
} 