package com.cloudmessenger.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * Класс для хранения конфигурационных свойств приложения
 */
@Component
@PropertySource("classpath:application.properties")
public class ConfigProperties {

    @Value("${upload.dir:${catalina.home}/uploads}")
    private String uploadDir;
    
    @Value("${upload.max.size:10485760}") // 10MB по умолчанию
    private long maxFileSize;
    
    @Value("${allowed.file.types:image/jpeg,image/png,image/gif,audio/mpeg,audio/wav,video/mp4,application/pdf,application/vnd.openxmlformats-officedocument.wordprocessingml.document}")
    private String allowedFileTypes;
    
    @Value("${twilio.account.sid}")
    private String twilioAccountSid;
    
    @Value("${twilio.auth.token}")
    private String twilioAuthToken;
    
    @Value("${twilio.phone.number}")
    private String twilioPhoneNumber;
    
    public String getUploadDir() {
        return uploadDir;
    }
    
    public void setUploadDir(String uploadDir) {
        this.uploadDir = uploadDir;
    }
    
    public long getMaxFileSize() {
        return maxFileSize;
    }
    
    public void setMaxFileSize(long maxFileSize) {
        this.maxFileSize = maxFileSize;
    }
    
    public String getAllowedFileTypes() {
        return allowedFileTypes;
    }
    
    public void setAllowedFileTypes(String allowedFileTypes) {
        this.allowedFileTypes = allowedFileTypes;
    }
    
    public String[] getAllowedFileTypesArray() {
        return allowedFileTypes.split(",");
    }
    
    public String getTwilioAccountSid() {
        return twilioAccountSid;
    }
    
    public void setTwilioAccountSid(String twilioAccountSid) {
        this.twilioAccountSid = twilioAccountSid;
    }
    
    public String getTwilioAuthToken() {
        return twilioAuthToken;
    }
    
    public void setTwilioAuthToken(String twilioAuthToken) {
        this.twilioAuthToken = twilioAuthToken;
    }
    
    public String getTwilioPhoneNumber() {
        return twilioPhoneNumber;
    }
    
    public void setTwilioPhoneNumber(String twilioPhoneNumber) {
        this.twilioPhoneNumber = twilioPhoneNumber;
    }
} 