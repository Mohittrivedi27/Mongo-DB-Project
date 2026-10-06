package com.jobautomation.controller;

import com.jobautomation.dto.RolesUpdateRequest;
import com.jobautomation.dto.SkillsUpdateRequest;
import com.jobautomation.model.User;
import com.jobautomation.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
@Tag(name = "Profile Management", description = "Candidate profile, skills, and target role endpoints")
@SecurityRequirement(name = "bearerAuth")
public class ProfileController {

    private final UserService userService;

    public ProfileController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @Operation(summary = "Get current candidate profile")
    public ResponseEntity<User> getProfile(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(userService.getUserByEmail(getUserEmail(userDetails)));
    }

    @PutMapping
    @Operation(summary = "Update personal profile information")
    public ResponseEntity<User> updateProfile(@AuthenticationPrincipal UserDetails userDetails,
                                               @RequestBody User profileUpdate) {
        return ResponseEntity.ok(userService.updateProfile(getUserEmail(userDetails), profileUpdate));
    }

    @PutMapping("/skills")
    @Operation(summary = "Update candidate technical skills list")
    public ResponseEntity<User> updateSkills(@AuthenticationPrincipal UserDetails userDetails,
                                              @RequestBody SkillsUpdateRequest request) {
        return ResponseEntity.ok(userService.updateSkills(getUserEmail(userDetails), request.getSkills()));
    }

    @PutMapping("/roles")
    @Operation(summary = "Update candidate preferred target job roles")
    public ResponseEntity<User> updatePreferredRoles(@AuthenticationPrincipal UserDetails userDetails,
                                                     @RequestBody RolesUpdateRequest request) {
        return ResponseEntity.ok(userService.updatePreferredRoles(getUserEmail(userDetails), request.getPreferredRoles()));
    }

    private String getUserEmail(UserDetails userDetails) {
        if (userDetails != null) return userDetails.getUsername();
        return "demo@jobautomation.com"; // Fallback for testing unauthenticated state
    }
}
