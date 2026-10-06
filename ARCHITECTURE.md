# ARCHITECTURE & SYSTEM DESIGN
## Automated System for Personalized Job Applications

### 1. Architectural Overview

The Automated System for Personalized Job Applications is a multi-tier, event-driven enterprise application designed to automate, customize, and track job applications for students and job seekers.

```
+-----------------------------------------------------------------------+
|                             React.js Frontend                         |
|      Vite + React Router + Axios + Reusable Custom CSS Components    |
+-----------------------------------------------------------------------+
                                   |
                                   | REST API (JWT Authenticated)
                                   v
+-----------------------------------------------------------------------+
|                         Spring Boot 3 REST APIs                       |
|   Controllers -> Services -> Repositories -> Data Mappers / DTOs      |
+-----------------------------------------------------------------------+
        |                  |                     |               |
        v                  v                     v               v
+---------------+  +---------------+  +-------------------+  +----------+
| MongoDB Atlas |  |  Redis Cache  |  |   Apache Kafka    |  |  Google  |
| Primary Data  |  | Rate Limiter  |  |  Event Processing |  |  Sheets  |
| Store & Indexes| | Lock & Counters| | Application Queue |  |   API    |
+---------------+  +---------------+  +-------------------+  +----------+
                                                 |
                                                 v
                                    +--------------------------+
                                    | Kafka Email Consumer     |
                                    | Rate Limiter Validation  |
                                    +--------------------------+
                                                 |
                                                 v
                                    +--------------------------+
                                    | Email Provider / SMTP    |
                                    | Attachment & Personalize |
                                    +--------------------------+
                                                 |
                                                 v
                                    +--------------------------+
                                    | Recruiter / HR Inbox     |
                                    +--------------------------+
```

---

### 2. MongoDB Data Models & Schema Design

#### Collection: `users`
- `id`: String (ObjectId)
- `name`: String
- `email`: String (Unique Index)
- `passwordHash`: String (BCrypt)
- `phone`: String
- `location`: String
- `emailVerified`: boolean (default: false)
- `verificationToken`: String (6-digit OTP code)
- `verificationTokenExpiry`: Instant (24 hours TTL)
- `education`: List<Education> (degree, institution, startYear, endYear, gpa)
- `experience`: List<Experience> (company, role, duration, description)
- `skills`: List<String> (Indexable)
- `preferredRoles`: List<String>
- `preferredLocations`: List<String>
- `portfolioUrl`: String
- `githubUrl`: String
- `linkedinUrl`: String
- `dailyLimit`: int (default: 50)
- `hourlyLimit`: int (default: 10)
- `scheduleStartTime`: String (HH:mm)
- `scheduleEndTime`: String (HH:mm)
- `createdAt`: Instant
- `updatedAt`: Instant

#### Collection: `resumes`
- `id`: String (ObjectId)
- `userId`: String (Index)
- `name`: String
- `type`: String (e.g. "Java Backend", "Full Stack", "Data Science")
- `associatedRoles`: List<String>
- `fileStorageReference`: String (path or gridFS id)
- `fileName`: String
- `contentType`: String
- `fileSize`: long
- `createdAt`: Instant
- `active`: boolean

#### Collection: `jobs`
- `id`: String (ObjectId)
- `companyName`: String (Index)
- `jobTitle`: String (Index)
- `description`: String
- `requiredSkills`: List<String> (Index)
- `preferredSkills`: List<String>
- `location`: String
- `workMode`: String (REMOTE, HYBRID, ON_SITE)
- `experienceRequired`: String
- `salary`: String
- `jobUrl`: String
- `source`: String (MANUAL, GOOGLE_SHEETS, PUBLIC)
- `postedDate`: Instant
- `expiryDate`: Instant
- `status`: String (OPEN, CLOSED)
- `createdAt`: Instant
- `updatedAt`: Instant

#### Collection: `hr_contacts`
- `id`: String (ObjectId)
- `companyName`: String (Index)
- `name`: String
- `email`: String (Index)
- `designation`: String
- `source`: String (MANUAL, GOOGLE_SHEETS, PUBLIC)
- `verificationStatus`: String (VERIFIED, UNVERIFIED, BOUNCED)
- `associatedJobIds`: List<String>
- `createdAt`: Instant

#### Collection: `applications`
- `id`: String (ObjectId)
- `userId`: String (Index)
- `jobId`: String (Index)
- `hrContactId`: String (Index)
- `resumeId`: String
- `companyName`: String
- `jobTitle`: String
- `matchScore`: double
- `matchDetails`: Map<String, Object>
- `emailSubject`: String
- `emailBody`: String
- `status`: String (DRAFT, MATCHED, READY, APPROVED, QUEUED, RATE_LIMITED, PROCESSING, SENT, DELIVERED, REPLIED, INTERVIEW, REJECTED, OFFER, FAILED)
- `scheduledFor`: Instant
- `approvedAt`: Instant
- `queuedAt`: Instant
- `sentAt`: Instant
- `repliedAt`: Instant
- `createdAt`: Instant (Index)
- `updatedAt`: Instant

#### Collection: `email_templates`
- `id`: String (ObjectId)
- `userId`: String (Index)
- `name`: String
- `subject`: String
- `body`: String
- `isDefault`: boolean
- `createdAt`: Instant

#### Collection: `email_logs`
- `id`: String (ObjectId)
- `applicationId`: String (Index)
- `userId`: String (Index)
- `recipientEmail`: String
- `subject`: String
- `status`: String (QUEUED, SENT, DELIVERED, BOUNCED, FAILED)
- `errorMessage`: String
- `sentAt`: Instant

#### Collection: `application_events`
- `id`: String (ObjectId)
- `applicationId`: String (Index)
- `eventType`: String
- `description`: String
- `timestamp`: Instant

#### Collection: `job_matches`
- `id`: String (ObjectId)
- `userId`: String (Index)
- `jobId`: String (Index)
- `matchScore`: double
- `matchedSkills`: List<String>
- `missingSkills`: List<String>
- `recommendedResumeId`: String
- `calculatedAt`: Instant

#### Collection: `sync_records`
- `id`: String (ObjectId)
- `userId`: String (Index)
- `spreadsheetId`: String
- `syncType`: String (IMPORT_JOBS, IMPORT_HR, EXPORT_APPLICATIONS)
- `recordsProcessed`: int
- `recordsAdded`: int
- `recordsFailed`: int
- `status`: String (SUCCESS, PARTIAL_SUCCESS, FAILED)
- `errorMessage`: String
- `timestamp`: Instant

---

### 3. Kafka Messaging Architecture

- **Topics**:
  - `application.created`: Published when a user matches or creates an application.
  - `email.requested`: Published when an application is approved and ready for sending.
  - `email.sent`: Published when SMTP sending succeeds.
  - `email.failed`: Published when sending encounters an error.
  - `application.status.changed`: Published for real-time status updates and Google Sheets sync triggers.

---

### 4. Redis Rate Limiting & Lock Architecture

- **Keys**:
  - `rate:user:{userId}:hourly`: Incremented counter with 1-hour TTL.
  - `rate:user:{userId}:daily`: Incremented counter with 24-hour TTL.
  - `lock:application:{applicationId}`: Distributed lock (5-minute TTL) to guarantee idempotent sending.

---

### 5. Backend Package Structure (`com.jobautomation`)

- `controller`: REST Endpoints (`AuthController`, `ProfileController`, `ResumeController`, `JobController`, `HRContactController`, `ApplicationController`, `JobMatchController`, `EmailTemplateController`, `GoogleSheetsController`, `DashboardController`, `SettingsController`)
- `service`: Core business logic (`UserService`, `ResumeService`, `JobService`, `HRContactService`, `ApplicationService`, `JobMatchingService`, `EmailService`, `RateLimitingService`, `GoogleSheetsSyncService`)
- `repository`: Spring Data Mongo interfaces
- `model`: Mongo documents & nested entity classes
- `dto`: Request and response data transfer objects
- `mapper`: Entity-DTO converters
- `security`: JWT filter, UserDetailsService, SecurityConfig, PasswordEncoder
- `config`: Mongo, Redis, Kafka, OpenAPI, Mail configuration
- `exception`: GlobalExceptionHandler, Custom Exceptions
- `integration`: Google Sheets API client abstraction
- `scheduler`: Scheduled email sender & status sync jobs
- `messaging`: Kafka producers, consumers, and event DTOs
- `email`: SMTP mail sender & template renderer
- `matching`: Configurable scoring engine algorithms

---

### 6. Frontend Architecture (React.js + Vite)

- **State Management**: React Context (`AuthContext`) + Custom Hooks
- **Routing**: React Router DOM (Protected & Public routes)
- **HTTP Client**: Axios with request/response interceptors for Bearer JWT injection & centralized error handling
- **CSS Strategy**: Clean modular CSS with CSS variables (`src/styles/theme.css` and component-level CSS files)
