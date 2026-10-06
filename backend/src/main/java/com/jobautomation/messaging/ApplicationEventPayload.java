package com.jobautomation.messaging;

public class ApplicationEventPayload {
    private String applicationId;
    private String userId;
    private String eventType;
    private String recipientEmail;
    private String subject;
    private String body;
    private String resumeFilePath;

    public ApplicationEventPayload() {}

    public ApplicationEventPayload(String applicationId, String userId, String eventType) {
        this.applicationId = applicationId;
        this.userId = userId;
        this.eventType = eventType;
    }

    public String getApplicationId() { return applicationId; }
    public void setApplicationId(String applicationId) { this.applicationId = applicationId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }

    public String getRecipientEmail() { return recipientEmail; }
    public void setRecipientEmail(String recipientEmail) { this.recipientEmail = recipientEmail; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }

    public String getResumeFilePath() { return resumeFilePath; }
    public void setResumeFilePath(String resumeFilePath) { this.resumeFilePath = resumeFilePath; }
}
