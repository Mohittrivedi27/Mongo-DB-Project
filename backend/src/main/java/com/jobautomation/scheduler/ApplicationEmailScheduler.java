package com.jobautomation.scheduler;

import com.jobautomation.messaging.KafkaEmailConsumer;
import com.jobautomation.model.Application;
import com.jobautomation.repository.ApplicationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ApplicationEmailScheduler {

    private static final Logger log = LoggerFactory.getLogger(ApplicationEmailScheduler.class);

    private final ApplicationRepository applicationRepository;
    private final KafkaEmailConsumer kafkaEmailConsumer;

    public ApplicationEmailScheduler(ApplicationRepository applicationRepository, KafkaEmailConsumer kafkaEmailConsumer) {
        this.applicationRepository = applicationRepository;
        this.kafkaEmailConsumer = kafkaEmailConsumer;
    }

    @Scheduled(fixedRate = 60000) // Runs every minute
    public void processQueuedEmails() {
        List<Application> queuedApplications = applicationRepository.findByStatus("QUEUED");
        if (queuedApplications.isEmpty()) return;

        log.info("Scheduler processing {} queued applications...", queuedApplications.size());
        for (Application app : queuedApplications) {
            kafkaEmailConsumer.processEmailSend(app.getId());
        }
    }
}
