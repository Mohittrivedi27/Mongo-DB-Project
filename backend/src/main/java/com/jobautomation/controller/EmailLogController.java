package com.jobautomation.controller;

import com.jobautomation.model.EmailLog;
import com.jobautomation.model.User;
import com.jobautomation.service.EmailService;
import com.jobautomation.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/email-logs")
@Tag(name = "Email Logs", description = "Email queue & delivery audit logs")
@SecurityRequirement(name = "bearerAuth")
public class EmailLogController {

    private final EmailService emailService;
    private final UserService userService;

    public EmailLogController(EmailService emailService, UserService userService) {
        this.emailService = emailService;
        this.userService = userService;
    }

    @GetMapping
    @Operation(summary = "Get email logs for candidate applications")
    public ResponseEntity<List<EmailLog>> getEmailLogs(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.getUserByEmail(getUserEmail(userDetails));
        return ResponseEntity.ok(emailService.getUserEmailLogs(user.getId()));
    }

    private String getUserEmail(UserDetails userDetails) {
        if (userDetails != null) return userDetails.getUsername();
        return "demo@jobautomation.com";
    }
}
