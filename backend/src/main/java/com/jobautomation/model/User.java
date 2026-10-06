package com.jobautomation.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "users")
public class User {

    @Id
    private String id;

    private String name;

    @Indexed(unique = true)
    private String email;

    private String passwordHash;
    private String phone;
    private String location;

    private boolean emailVerified = false;
    private String verificationToken;
    private Instant verificationTokenExpiry;

    private List<Education> education = new ArrayList<>();
    private List<Experience> experience = new ArrayList<>();

    @Indexed
    private List<String> skills = new ArrayList<>();
    private List<String> preferredRoles = new ArrayList<>();
    private List<String> preferredLocations = new ArrayList<>();

    private String portfolioUrl;
    private String githubUrl;
    private String linkedinUrl;

    private int dailyLimit = 50;
    private int hourlyLimit = 10;
    private String scheduleStartTime = "09:00";
    private String scheduleEndTime = "17:00";

    private Instant createdAt = Instant.now();
    private Instant updatedAt = Instant.now();

    public User() {}

    public User(String name, String email, String passwordHash) {
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public boolean isEmailVerified() { return emailVerified; }
    public void setEmailVerified(boolean emailVerified) { this.emailVerified = emailVerified; }

    public String getVerificationToken() { return verificationToken; }
    public void setVerificationToken(String verificationToken) { this.verificationToken = verificationToken; }

    public Instant getVerificationTokenExpiry() { return verificationTokenExpiry; }
    public void setVerificationTokenExpiry(Instant verificationTokenExpiry) { this.verificationTokenExpiry = verificationTokenExpiry; }

    public List<Education> getEducation() { return education; }
    public void setEducation(List<Education> education) { this.education = education; }

    public List<Experience> getExperience() { return experience; }
    public void setExperience(List<Experience> experience) { this.experience = experience; }

    public List<String> getSkills() { return skills; }
    public void setSkills(List<String> skills) { this.skills = skills; }

    public List<String> getPreferredRoles() { return preferredRoles; }
    public void setPreferredRoles(List<String> preferredRoles) { this.preferredRoles = preferredRoles; }

    public List<String> getPreferredLocations() { return preferredLocations; }
    public void setPreferredLocations(List<String> preferredLocations) { this.preferredLocations = preferredLocations; }

    public String getPortfolioUrl() { return portfolioUrl; }
    public void setPortfolioUrl(String portfolioUrl) { this.portfolioUrl = portfolioUrl; }

    public String getGithubUrl() { return githubUrl; }
    public void setGithubUrl(String githubUrl) { this.githubUrl = githubUrl; }

    public String getLinkedinUrl() { return linkedinUrl; }
    public void setLinkedinUrl(String linkedinUrl) { this.linkedinUrl = linkedinUrl; }

    public int getDailyLimit() { return dailyLimit; }
    public void setDailyLimit(int dailyLimit) { this.dailyLimit = dailyLimit; }

    public int getHourlyLimit() { return hourlyLimit; }
    public void setHourlyLimit(int hourlyLimit) { this.hourlyLimit = hourlyLimit; }

    public String getScheduleStartTime() { return scheduleStartTime; }
    public void setScheduleStartTime(String scheduleStartTime) { this.scheduleStartTime = scheduleStartTime; }

    public String getScheduleEndTime() { return scheduleEndTime; }
    public void setScheduleEndTime(String scheduleEndTime) { this.scheduleEndTime = scheduleEndTime; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
