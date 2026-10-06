package com.jobautomation.controller;

import com.jobautomation.model.Resume;
import com.jobautomation.service.ResumeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/resumes")
@Tag(name = "Resume Repository", description = "Upload, manage, and delete domain resumes")
@SecurityRequirement(name = "bearerAuth")
public class ResumeController {

    private final ResumeService resumeService;

    public ResumeController(ResumeService resumeService) {
        this.resumeService = resumeService;
    }

    @GetMapping
    @Operation(summary = "Get candidate domain resumes")
    public ResponseEntity<List<Resume>> getResumes(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(resumeService.getUserResumes(getUserEmail(userDetails)));
    }

    @PostMapping
    @Operation(summary = "Upload a domain resume file")
    public ResponseEntity<Resume> uploadResume(@AuthenticationPrincipal UserDetails userDetails,
                                                @RequestParam("name") String name,
                                                @RequestParam("type") String type,
                                                @RequestParam(value = "associatedRoles", defaultValue = "") String associatedRoles,
                                                @RequestParam("file") MultipartFile file) throws IOException {
        return ResponseEntity.ok(resumeService.uploadResume(getUserEmail(userDetails), name, type, associatedRoles, file));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete domain resume")
    public ResponseEntity<Void> deleteResume(@AuthenticationPrincipal UserDetails userDetails,
                                              @PathVariable String id) {
        resumeService.deleteResume(getUserEmail(userDetails), id);
        return ResponseEntity.noContent().build();
    }

    private String getUserEmail(UserDetails userDetails) {
        if (userDetails != null) return userDetails.getUsername();
        return "demo@jobautomation.com";
    }
}
