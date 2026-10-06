package com.jobautomation.controller;

import com.jobautomation.model.EmailTemplate;
import com.jobautomation.model.User;
import com.jobautomation.repository.EmailTemplateRepository;
import com.jobautomation.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/email-templates")
@Tag(name = "Email Templates", description = "Personalized email template endpoints")
@SecurityRequirement(name = "bearerAuth")
public class EmailTemplateController {

    private final EmailTemplateRepository emailTemplateRepository;
    private final UserService userService;

    public EmailTemplateController(EmailTemplateRepository emailTemplateRepository, UserService userService) {
        this.emailTemplateRepository = emailTemplateRepository;
        this.userService = userService;
    }

    @GetMapping
    @Operation(summary = "Get candidate email templates")
    public ResponseEntity<List<EmailTemplate>> getTemplates(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.getUserByEmail(getUserEmail(userDetails));
        return ResponseEntity.ok(emailTemplateRepository.findByUserId(user.getId()));
    }

    @PostMapping
    @Operation(summary = "Save or update personalized email template")
    public ResponseEntity<EmailTemplate> saveTemplate(@AuthenticationPrincipal UserDetails userDetails,
                                                      @RequestBody EmailTemplate template) {
        User user = userService.getUserByEmail(getUserEmail(userDetails));
        template.setUserId(user.getId());
        template.setCreatedAt(Instant.now());
        return ResponseEntity.ok(emailTemplateRepository.save(template));
    }

    private String getUserEmail(UserDetails userDetails) {
        if (userDetails != null) return userDetails.getUsername();
        return "demo@jobautomation.com";
    }
}
