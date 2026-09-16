package com.techhub.notification_service.service;

public interface EmailService {

    void sendWelcomeEmail(String to, String displayName, String role);

    void sendPasswordChangedEmail(String to, String displayName);
}