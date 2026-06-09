package com.techhub.notification_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class MailConfig {

    @Value("${spring.mail.host}")
    private String host;

    @Value("${spring.mail.port}")
    private int port;

    @Value("${spring.mail.username}")
    private String username;

    @Value("${spring.mail.password}")
    private String password;

    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(host);
        mailSender.setPort(port);
        mailSender.setUsername(username);
        mailSender.setPassword(password);

        // Enable auth and TLS only when real credentials are provided.
        // Empty username = MailHog dev relay (no auth, no TLS).
        boolean hasCredentials = username != null && !username.isBlank();

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth",                  String.valueOf(hasCredentials));
        props.put("mail.smtp.starttls.enable",        String.valueOf(hasCredentials));
        props.put("mail.smtp.starttls.required",      String.valueOf(hasCredentials));
        props.put("mail.debug", "false");

        return mailSender;
    }
}