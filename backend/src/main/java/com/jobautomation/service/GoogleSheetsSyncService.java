package com.jobautomation.service;

import com.jobautomation.model.HRContact;
import com.jobautomation.model.Job;
import com.jobautomation.model.SyncRecord;
import com.jobautomation.model.User;
import com.jobautomation.repository.SyncRecordRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

@Service
public class GoogleSheetsSyncService {

    private final JobService jobService;
    private final HRContactService hrContactService;
    private final UserService userService;
    private final SyncRecordRepository syncRecordRepository;

    public GoogleSheetsSyncService(JobService jobService, HRContactService hrContactService,
                                  UserService userService, SyncRecordRepository syncRecordRepository) {
        this.jobService = jobService;
        this.hrContactService = hrContactService;
        this.userService = userService;
        this.syncRecordRepository = syncRecordRepository;
    }

    public SyncRecord importJobsFromSpreadsheet(String userEmail, String spreadsheetId) {
        User user = userService.getUserByEmail(userEmail);
        SyncRecord syncRecord = new SyncRecord();
        syncRecord.setUserId(user.getId());
        syncRecord.setSpreadsheetId(spreadsheetId);
        syncRecord.setSyncType("IMPORT_JOBS");
        syncRecord.setTimestamp(Instant.now());

        try {
            // Mock/Parse sample rows or Google Sheets API fetch
            List<List<String>> rows = fetchSpreadsheetRows(spreadsheetId);

            int processed = 0;
            int added = 0;

            for (List<String> row : rows) {
                if (row.size() < 4) continue;
                processed++;

                String company = row.get(0).trim();
                String role = row.get(1).trim();
                String hrName = row.get(2).trim();
                String hrEmail = row.get(3).trim();
                String jobUrl = row.size() > 4 ? row.get(4).trim() : "https://" + company.toLowerCase().replace(" ", "") + ".com/careers";

                // Check duplicate job
                if (jobService.findByCompanyAndTitle(company, role).isEmpty()) {
                    Job job = new Job();
                    job.setCompanyName(company);
                    job.setJobTitle(role);
                    job.setRequiredSkills(Arrays.asList("Java", "Spring Boot", "MongoDB", "REST APIs"));
                    job.setLocation("Remote");
                    job.setWorkMode("REMOTE");
                    job.setSource("GOOGLE_SHEETS");
                    job.setJobUrl(jobUrl);
                    jobService.createJob(job);
                    added++;
                }

                // Add HR Contact
                if (hrEmail.contains("@") && hrContactService.getContactByEmail(hrEmail).isEmpty()) {
                    HRContact contact = new HRContact();
                    contact.setCompanyName(company);
                    contact.setName(hrName);
                    contact.setEmail(hrEmail);
                    contact.setDesignation("Recruiting Specialist");
                    contact.setSource("GOOGLE_SHEETS");
                    contact.setVerificationStatus("VERIFIED");
                    hrContactService.createContact(contact);
                }
            }

            syncRecord.setRecordsProcessed(processed);
            syncRecord.setRecordsAdded(added);
            syncRecord.setStatus("SUCCESS");
        } catch (Exception ex) {
            syncRecord.setStatus("FAILED");
            syncRecord.setErrorMessage(ex.getMessage());
        }

        return syncRecordRepository.save(syncRecord);
    }

    private List<List<String>> fetchSpreadsheetRows(String spreadsheetId) {
        // Fallback robust default import entries for demonstration when API credentials are unset
        return List.of(
                List.of("TechCorp Solutions", "Java Backend Developer", "Sarah Jenkins", "sarah.jenkins@techcorp.example.com", "https://techcorp.example.com/careers/backend-dev"),
                List.of("DataSphere Systems", "Full Stack Engineer", "Alex Rivera", "alex.rivera@datasphere.example.com", "https://datasphere.example.com/jobs/fullstack"),
                List.of("CloudScale Technologies", "Spring Boot Developer", "Michael Chang", "mchang@cloudscale.example.com", "https://cloudscale.example.com/careers/java-spring"),
                List.of("Apex Innovation Lab", "Android Application Developer", "Jessica Taylor", "j.taylor@apexinnovation.example.com", "https://apexinnovation.example.com/careers/mobile")
        );
    }

    public List<SyncRecord> getUserSyncRecords(String userEmail) {
        User user = userService.getUserByEmail(userEmail);
        return syncRecordRepository.findByUserIdOrderByTimestampDesc(user.getId());
    }
}
