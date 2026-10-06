package com.jobautomation.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "resumes")
public class Resume {

    @Id
    private String id;

    @Indexed
    private String userId;

    private String name;
    private String type; // e.g. "Java Backend", "Full Stack", "Android Developer"
    private List<String> associatedRoles = new ArrayList<>();
    private String fileStorageReference;
    private String fileName;
    private String contentType;
    private long fileSize;
    private boolean active = true;
    private Instant createdAt = Instant.now();

    public Resume() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public List<String> getAssociatedRoles() { return associatedRoles; }
    public void setAssociatedRoles(List<String> associatedRoles) { this.associatedRoles = associatedRoles; }

    public String getFileStorageReference() { return fileStorageReference; }
    public void setFileStorageReference(String fileStorageReference) { this.fileStorageReference = fileStorageReference; }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }

    public long getFileSize() { return fileSize; }
    public void setFileSize(long fileSize) { this.fileSize = fileSize; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
