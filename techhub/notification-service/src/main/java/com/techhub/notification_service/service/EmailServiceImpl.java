package com.techhub.notification_service.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender  javaMailSender;
    private final TemplateService templateService;

    @Value("${app.mail.from}")
    private String fromAddress;

    @Value("${app.name}")
    private String appName;

    // ── Public API ───────────────────────────────────────────────────────────

    @Override
    public void sendWelcomeEmail(String to, String displayName, String role) {
        Map<String, Object> vars = new HashMap<>();
        vars.put("displayName", displayName);
        vars.put("role",        role != null ? role : "Member");
        vars.put("appName",     appName);
        vars.put("loginUrl",    "https://techhub.com/login");

        String subject = "Welcome to " + appName + ", " + displayName + "!";
        String html    = templateService.render("welcome", vars);

        send(to, subject, html);
        log.info("[EmailService] Welcome email sent to={}", to);
    }

    @Override
    public void sendPasswordChangedEmail(String to, String displayName) {
        Map<String, Object> vars = new HashMap<>();
        vars.put("displayName",  displayName);
        vars.put("appName",      appName);
        vars.put("supportEmail", fromAddress);
        vars.put("resetUrl",     "https://techhub.com/reset-password");

        String subject = appName + " — Your password was changed";
        String html    = templateService.render("password-changed", vars);

        send(to, subject, html);
        log.info("[EmailService] Password-changed email sent to={}", to);
    }

    // ── Private helpers ──────────────────────────────────────────────────────

    private void send(String to, String subject, String htmlBody) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromAddress);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true); // true = isHtml

            javaMailSender.send(message);
        } catch (MessagingException e) {
            log.error("[EmailService] Failed to send email to={} subject='{}' reason={}",
                    to, subject, e.getMessage());
            // Rethrow so NotificationServiceImpl can mark the record FAILED
            throw new RuntimeException("Email delivery failed: " + e.getMessage(), e);
        }
    }
}