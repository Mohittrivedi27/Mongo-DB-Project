package com.jobautomation.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobautomation.model.Application;
import com.jobautomation.model.User;
import com.jobautomation.repository.ApplicationRepository;
import com.jobautomation.repository.UserRepository;
import com.jobautomation.service.EmailService;
import com.jobautomation.service.RateLimitingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;

@Component
public class KafkaEmailConsumer {

    private static final Logger log = LoggerFactory.getLogger(KafkaEmailConsumer.class);

    private final EmailService emailService;
    private final RateLimitingService rateLimitingService;
    private final ApplicationRepository applicationRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    public KafkaEmailConsumer(EmailService emailService, RateLimitingService rateLimitingService,
                              ApplicationRepository applicationRepository, UserRepository userRepository,
                              ObjectMapper objectMapper) {
        this.emailService = emailService;
        this.rateLimitingService = rateLimitingService;
        this.applicationRepository = applicationRepository;
        this.userRepository = userRepository;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "email.requested", groupId = "job-application-group")
    public void consumeEmailRequested(String message) {
        try {
            ApplicationEventPayload payload = objectMapper.readValue(message, ApplicationEventPayload.class);
            log.info("Kafka Consumer received email.requested event for app: {}", payload.getApplicationId());

            processEmailSend(payload.getApplicationId());
        } catch (Exception ex) {
            log.error("Failed to process Kafka email message", ex);
        }
    }

    public void processEmailSend(String applicationId) {
        if (!rateLimitingService.acquireSendLock(applicationId)) {
            log.warn("Application [{}] is currently locked or sending in progress (idempotency check)", applicationId);
            return;
        }

        try {
            Optional<Application> appOpt = applicationRepository.findById(applicationId);
            if (appOpt.isEmpty()) return;

            Application app = appOpt.get();
            if ("SENT".equals(app.getStatus())) {
                log.info("Application [{}] already SENT. Skipping duplicate send.", applicationId);
                return;
            }

            Optional<User> userOpt = userRepository.findById(app.getUserId());
            if (userOpt.isEmpty()) return;

            User user = userOpt.get();

            // Check Redis Rate Limiter
            try {
                rateLimitingService.checkAndIncrementRateLimit(user);
            } catch (Exception ex) {
                app.setStatus("RATE_LIMITED");
                app.setUpdatedAt(Instant.now());
                applicationRepository.save(app);
                log.warn("Application [{}] RATE_LIMITED: {}", applicationId, ex.getMessage());
                return;
            }

            // Execute Real SMTP Send
            emailService.sendApplicationEmail(app, payloadResumeFilePath(app));

            app.setStatus("SENT");
            app.setSentAt(Instant.now());
            app.setUpdatedAt(Instant.now());
            applicationRepository.save(app);

            log.info("Successfully processed and sent email for application [{}]", applicationId);
        } finally {
            rateLimitingService.releaseSendLock(applicationId);
        }
    }

    private String payloadResumeFilePath(Application app) {
        return null;
    }
}
