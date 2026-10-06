package com.jobautomation.controller;

import com.jobautomation.model.Job;
import com.jobautomation.model.JobMatch;
import com.jobautomation.service.JobMatchingService;
import com.jobautomation.service.JobService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
@Tag(name = "Jobs & Matching", description = "Job directory management and job matching engine endpoints")
@SecurityRequirement(name = "bearerAuth")
public class JobController {

    private final JobService jobService;
    private final JobMatchingService jobMatchingService;

    public JobController(JobService jobService, JobMatchingService jobMatchingService) {
        this.jobService = jobService;
        this.jobMatchingService = jobMatchingService;
    }

    @GetMapping
    @Operation(summary = "Get all job opportunities")
    public ResponseEntity<List<Job>> getAllJobs() {
        return ResponseEntity.ok(jobService.getAllJobs());
    }

    @GetMapping("/page")
    @Operation(summary = "Get paginated job opportunities")
    public ResponseEntity<Page<Job>> getJobsPaginated(@RequestParam(defaultValue = "0") int page,
                                                      @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(jobService.getJobsPaginated(page, size));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get job details by ID")
    public ResponseEntity<Job> getJobById(@PathVariable String id) {
        return ResponseEntity.ok(jobService.getJobById(id));
    }

    @PostMapping
    @Operation(summary = "Create a new job opportunity")
    public ResponseEntity<Job> createJob(@RequestBody Job job) {
        return ResponseEntity.ok(jobService.createJob(job));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing job opportunity")
    public ResponseEntity<Job> updateJob(@PathVariable String id, @RequestBody Job job) {
        return ResponseEntity.ok(jobService.updateJob(id, job));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a job opportunity")
    public ResponseEntity<Void> deleteJob(@PathVariable String id) {
        jobService.deleteJob(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/matched")
    @Operation(summary = "Get calculated job match scores for candidate")
    public ResponseEntity<List<JobMatch>> getMatchedJobs(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(jobMatchingService.getUserMatches(getUserEmail(userDetails)));
    }

    @PostMapping("/matched/calculate")
    @Operation(summary = "Re-run matching engine calculation for candidate skills & target roles")
    public ResponseEntity<List<JobMatch>> recalculateMatches(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(jobMatchingService.calculateAndSaveMatchesForUser(getUserEmail(userDetails)));
    }

    private String getUserEmail(UserDetails userDetails) {
        if (userDetails != null) return userDetails.getUsername();
        return "demo@jobautomation.com";
    }
}
