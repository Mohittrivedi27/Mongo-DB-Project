package com.jobautomation.controller;

import com.jobautomation.dto.SettingsDto;
import com.jobautomation.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/settings")
@Tag(name = "Settings", description = "Application rate limit and schedule settings")
@SecurityRequirement(name = "bearerAuth")
public class SettingsController {

    private final UserService userService;

    public SettingsController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @Operation(summary = "Get rate limits and scheduler settings")
    public ResponseEntity<SettingsDto> getSettings(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(userService.getSettings(getUserEmail(userDetails)));
    }

    @PutMapping
    @Operation(summary = "Update rate limits and active scheduler hours")
    public ResponseEntity<SettingsDto> updateSettings(@AuthenticationPrincipal UserDetails userDetails,
                                                       @RequestBody SettingsDto settingsDto) {
        return ResponseEntity.ok(userService.updateSettings(getUserEmail(userDetails), settingsDto));
    }

    private String getUserEmail(UserDetails userDetails) {
        if (userDetails != null) return userDetails.getUsername();
        return "demo@jobautomation.com";
    }
}
