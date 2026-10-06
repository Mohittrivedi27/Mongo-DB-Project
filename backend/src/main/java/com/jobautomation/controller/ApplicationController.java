package com.jobautomation.controller;

import com.jobautomation.model.Application;
import com.jobautomation.model.ApplicationEvent;
import com.jobautomation.service.ApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/applications")
@Tag(name = "Applications", description = "Application drafting, approval, send queue, and tracking endpoints")
@SecurityRequirement(name = "bearerAuth")
public class ApplicationController {

    private final ApplicationService applicationService;

    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @GetMapping
    @Operation(summary = "Get candidate applications list")
    public ResponseEntity<List<Application>> getUserApplications(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(applicationService.getUserApplications(getUserEmail(userDetails)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get application details by ID")
    public ResponseEntity<Application> getApplicationById(@AuthenticationPrincipal UserDetails userDetails,
                                                          @PathVariable String id) {
        return ResponseEntity.ok(applicationService.getApplicationById(getUserEmail(userDetails), id));
    }

    @PostMapping
    @Operation(summary = "Draft a new personalized application")
    public ResponseEntity<Application> createApplication(@AuthenticationPrincipal UserDetails userDetails,
                                                         @RequestBody Map<String, String> payload) {
        String jobId = payload.get("jobId");
        String hrContactId = payload.get("hrContactId");
        String resumeId = payload.get("resumeId");
        return ResponseEntity.ok(applicationService.createApplication(getUserEmail(userDetails), jobId, hrContactId, resumeId));
    }

    @PostMapping("/{id}/approve")
    @Operation(summary = "Explicit candidate review & approval step")
    public ResponseEntity<Application> approveApplication(@AuthenticationPrincipal UserDetails userDetails,
                                                          @PathVariable String id,
                                                          @RequestBody Map<String, String> payload) {
        String subject = payload.get("emailSubject");
        String body = payload.get("emailBody");
        String resumeId = payload.get("resumeId");
        return ResponseEntity.ok(applicationService.approveApplication(getUserEmail(userDetails), id, subject, body, resumeId));
    }

    @PostMapping("/{id}/send")
    @Operation(summary = "Submit approved application to Kafka queue for SMTP delivery")
    public ResponseEntity<Application> sendApplication(@AuthenticationPrincipal UserDetails userDetails,
                                                       @PathVariable String id) {
        return ResponseEntity.ok(applicationService.sendApplication(getUserEmail(userDetails), id));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update application lifecycle status")
    public ResponseEntity<Application> updateStatus(@AuthenticationPrincipal UserDetails userDetails,
                                                    @PathVariable String id,
                                                    @RequestBody Map<String, String> payload) {
        String status = payload.get("status");
        return ResponseEntity.ok(applicationService.updateStatus(getUserEmail(userDetails), id, status));
    }

    @GetMapping("/{id}/events")
    @Operation(summary = "Get audit trail event log for application")
    public ResponseEntity<List<ApplicationEvent>> getApplicationEvents(@PathVariable String id) {
        return ResponseEntity.ok(applicationService.getApplicationEvents(id));
    }

    private String getUserEmail(UserDetails userDetails) {
        if (userDetails != null) return userDetails.getUsername();
        return "demo@jobautomation.com";
    }
}
