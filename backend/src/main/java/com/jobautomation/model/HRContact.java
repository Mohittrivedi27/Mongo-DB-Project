package com.jobautomation.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "hr_contacts")
public class HRContact {

    @Id
    private String id;

    @Indexed
    private String companyName;

    private String name;

    @Indexed
    private String email;

    private String designation;
    private String source; // MANUAL, GOOGLE_SHEETS, PUBLIC
    private String verificationStatus = "VERIFIED"; // VERIFIED, UNVERIFIED, BOUNCED

    private List<String> associatedJobIds = new ArrayList<>();
    private Instant createdAt = Instant.now();

    public HRContact() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getDesignation() { return designation; }
    public void setDesignation(String designation) { this.designation = designation; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public String getVerificationStatus() { return verificationStatus; }
    public void setVerificationStatus(String verificationStatus) { this.verificationStatus = verificationStatus; }

    public List<String> getAssociatedJobIds() { return associatedJobIds; }
    public void setAssociatedJobIds(List<String> associatedJobIds) { this.associatedJobIds = associatedJobIds; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
