package com.jobautomation.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "application_events")
public class ApplicationEvent {

    @Id
    private String id;

    @Indexed
    private String applicationId;

    private String eventType;
    private String description;
    private Instant timestamp = Instant.now();

    public ApplicationEvent() {}

    public ApplicationEvent(String applicationId, String eventType, String description) {
        this.applicationId = applicationId;
        this.eventType = eventType;
        this.description = description;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getApplicationId() { return applicationId; }
    public void setApplicationId(String applicationId) { this.applicationId = applicationId; }

    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }
}
