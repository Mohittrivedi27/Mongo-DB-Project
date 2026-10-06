package com.jobautomation.service;

import com.jobautomation.email.EmailTemplateEngine;
import com.jobautomation.exception.ResourceNotFoundException;
import com.jobautomation.messaging.ApplicationEventPayload;
import com.jobautomation.messaging.KafkaEmailConsumer;
import com.jobautomation.messaging.KafkaEventProducer;
import com.jobautomation.model.*;
import com.jobautomation.repository.*;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final ApplicationEventRepository applicationEventRepository;
    private final JobRepository jobRepository;
    private final HRContactRepository hrContactRepository;
    private final ResumeRepository resumeRepository;
    private final UserService userService;
    private final EmailTemplateEngine templateEngine;
    private final KafkaEventProducer kafkaEventProducer;
    private final KafkaEmailConsumer kafkaEmailConsumer;

    public ApplicationService(ApplicationRepository applicationRepository,
                              ApplicationEventRepository applicationEventRepository,
                              JobRepository jobRepository, HRContactRepository hrContactRepository,
                              ResumeRepository resumeRepository, UserService userService,
                              EmailTemplateEngine templateEngine, KafkaEventProducer kafkaEventProducer,
                              KafkaEmailConsumer kafkaEmailConsumer) {
        this.applicationRepository = applicationRepository;
        this.applicationEventRepository = applicationEventRepository;
        this.jobRepository = jobRepository;
        this.hrContactRepository = hrContactRepository;
        this.resumeRepository = resumeRepository;
        this.userService = userService;
        this.templateEngine = templateEngine;
        this.kafkaEventProducer = kafkaEventProducer;
        this.kafkaEmailConsumer = kafkaEmailConsumer;
    }

    public Application createApplication(String userEmail, String jobId, String hrContactId, String resumeId) {
        User user = userService.getUserByEmail(userEmail);

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found: " + jobId));

        HRContact hrContact = null;
        if (hrContactId != null && !hrContactId.isEmpty()) {
            hrContact = hrContactRepository.findById(hrContactId).orElse(null);
        }
        if (hrContact == null) {
            List<HRContact> companyContacts = hrContactRepository.findByCompanyName(job.getCompanyName());
            if (!companyContacts.isEmpty()) {
                hrContact = companyContacts.get(0);
            }
        }

        // Prevent duplicate applications for same user + job
        Optional<Application> existing = applicationRepository.findByUserId(user.getId()).stream()
                .filter(a -> jobId.equals(a.getJobId()))
                .findFirst();

        if (existing.isPresent()) {
            return existing.get();
        }

        Application app = new Application();
        app.setUserId(user.getId());
        app.setJobId(jobId);
        app.setCompanyName(job.getCompanyName());
        app.setJobTitle(job.getJobTitle());

        if (hrContact != null) {
            app.setHrContactId(hrContact.getId());
            app.setHrName(hrContact.getName());
            app.setHrEmail(hrContact.getEmail());
        } else {
            app.setHrName("Hiring Manager");
            app.setHrEmail("careers@" + job.getCompanyName().toLowerCase().replaceAll("[^a-z0-9]", "") + ".com");
        }

        if (resumeId != null && !resumeId.isEmpty()) {
            app.setResumeId(resumeId);
        }

        app.setMatchScore(85.0); // Default match score
        app.setStatus("DRAFT");

        // Generate personalized email
        String defaultTemplate = "Hi {{hrName}},\n\nI am {{candidateName}} and I am interested in the {{jobTitle}} opportunity at {{companyName}}.\n\nMy skills include {{skills}}.\n\nPlease find my resume attached for your consideration.\n\nPortfolio: {{portfolio}}\nGitHub: {{github}}\nLinkedIn: {{linkedin}}\n\nRegards,\n{{candidateName}}";
        String renderedSubject = "Application for " + job.getJobTitle() + " at " + job.getCompanyName();
        String renderedBody = templateEngine.render(defaultTemplate, user, app.getHrName(), app.getCompanyName(), app.getJobTitle());

        app.setEmailSubject(renderedSubject);
        app.setEmailBody(renderedBody);
        app.setCreatedAt(Instant.now());

        Application saved = applicationRepository.save(app);

        recordEvent(saved.getId(), "APPLICATION_CREATED", "Application draft created for " + job.getJobTitle());
        kafkaEventProducer.publishEvent("application.created", new ApplicationEventPayload(saved.getId(), user.getId(), "APPLICATION_CREATED"));

        return saved;
    }

    public Application approveApplication(String userEmail, String id, String subject, String body, String resumeId) {
        User user = userService.getUserByEmail(userEmail);
        Application app = applicationRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));

        if (subject != null) app.setEmailSubject(subject);
        if (body != null) app.setEmailBody(body);
        if (resumeId != null) app.setResumeId(resumeId);

        app.setStatus("APPROVED");
        app.setApprovedAt(Instant.now());
        app.setUpdatedAt(Instant.now());

        Application saved = applicationRepository.save(app);
        recordEvent(saved.getId(), "APPLICATION_APPROVED", "Application approved by candidate");

        return saved;
    }

    public Application sendApplication(String userEmail, String id) {
        User user = userService.getUserByEmail(userEmail);
        Application app = applicationRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));

        if (!user.isEmailVerified()) {
            throw new IllegalStateException("Your email address (" + user.getEmail() + ") is not verified. Please verify your email before dispatching automated applications.");
        }

        if (!"APPROVED".equals(app.getStatus()) && !"DRAFT".equals(app.getStatus()) && !"QUEUED".equals(app.getStatus())) {
            throw new IllegalStateException("Application must be approved prior to sending");
        }

        app.setStatus("QUEUED");
        app.setQueuedAt(Instant.now());
        app.setUpdatedAt(Instant.now());
        Application saved = applicationRepository.save(app);

        recordEvent(saved.getId(), "EMAIL_QUEUED", "Published email.requested event to Kafka queue");

        ApplicationEventPayload payload = new ApplicationEventPayload(saved.getId(), user.getId(), "EMAIL_REQUESTED");
        kafkaEventProducer.publishEvent("email.requested", payload);

        // Synchronous consumer processing call for immediate local verification
        kafkaEmailConsumer.processEmailSend(saved.getId());

        return applicationRepository.findById(saved.getId()).orElse(saved);
    }

    public Application updateStatus(String userEmail, String id, String newStatus) {
        User user = userService.getUserByEmail(userEmail);
        Application app = applicationRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));

        app.setStatus(newStatus);
        if ("SENT".equals(newStatus)) app.setSentAt(Instant.now());
        if ("REPLIED".equals(newStatus)) app.setRepliedAt(Instant.now());
        app.setUpdatedAt(Instant.now());

        Application saved = applicationRepository.save(app);
        recordEvent(saved.getId(), "STATUS_UPDATED", "Status changed to " + newStatus);

        return saved;
    }

    public List<Application> getUserApplications(String userEmail) {
        User user = userService.getUserByEmail(userEmail);
        return applicationRepository.findByUserId(user.getId());
    }

    public Application getApplicationById(String userEmail, String id) {
        User user = userService.getUserByEmail(userEmail);
        return applicationRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Application not found: " + id));
    }

    public List<ApplicationEvent> getApplicationEvents(String id) {
        return applicationEventRepository.findByApplicationIdOrderByTimestampAsc(id);
    }

    private void recordEvent(String applicationId, String eventType, String description) {
        ApplicationEvent event = new ApplicationEvent(applicationId, eventType, description);
        applicationEventRepository.save(event);
    }
}
