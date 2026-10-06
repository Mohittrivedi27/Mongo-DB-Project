package com.jobautomation.service;

import com.jobautomation.model.Application;
import com.jobautomation.model.EmailLog;
import com.jobautomation.repository.EmailLogRepository;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.Instant;
import java.util.List;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final EmailLogRepository emailLogRepository;

    @Value("${app.email.from:noreply@jobautomation.com}")
    private String fromEmail;

    public EmailService(JavaMailSender mailSender, EmailLogRepository emailLogRepository) {
        this.mailSender = mailSender;
        this.emailLogRepository = emailLogRepository;
    }

    public void sendApplicationEmail(Application application, String resumeFilePath) {
        EmailLog emailLog = new EmailLog();
        emailLog.setApplicationId(application.getId());
        emailLog.setUserId(application.getUserId());
        emailLog.setRecipientEmail(application.getHrEmail());
        emailLog.setSubject(application.getEmailSubject());
        emailLog.setStatus("QUEUED");

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(fromEmail);
            helper.setTo(application.getHrEmail());
            helper.setSubject(application.getEmailSubject());
            helper.setText(application.getEmailBody(), false);

            if (resumeFilePath != null) {
                File file = new File(resumeFilePath);
                if (file.exists()) {
                    helper.addAttachment(file.getName(), file);
                }
            }

            mailSender.send(message);

            emailLog.setStatus("SENT");
            emailLog.setSentAt(Instant.now());
        } catch (Exception ex) {
            emailLog.setStatus("FAILED");
            emailLog.setErrorMessage(ex.getMessage());
            // Log cleanly, do not throw fatal exception so queue state is preserved
        } finally {
            emailLogRepository.save(emailLog);
        }
    }

    public void sendVerificationEmail(String toEmail, String code) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false);

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("Your Verification Code - Automated System for Personalized Job Applications");
            helper.setText("Hello,\n\n"
                    + "Thank you for registering on the Automated Job Application System.\n"
                    + "Your 6-digit email verification code is:\n\n"
                    + "    " + code + "\n\n"
                    + "This code will expire in 24 hours.\n"
                    + "If you did not request this, please ignore this email.\n\n"
                    + "Best regards,\nJob Application Automation Team", false);

            mailSender.send(message);
        } catch (Exception ex) {
            // Log cleanly - do not block user registration if SMTP is mock/offline in local dev
            System.err.println("Could not send verification email: " + ex.getMessage());
        }
    }

    public List<EmailLog> getUserEmailLogs(String userId) {
        return emailLogRepository.findByUserId(userId);
    }
}
