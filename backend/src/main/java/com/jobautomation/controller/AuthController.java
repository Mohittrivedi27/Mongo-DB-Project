package com.jobautomation.controller;

import com.jobautomation.dto.AuthRequest;
import com.jobautomation.dto.AuthResponse;
import com.jobautomation.dto.RegisterRequest;
import com.jobautomation.service.UserService;
import com.jobautomation.dto.ResendVerificationRequest;
import com.jobautomation.dto.VerifyEmailRequest;
import com.jobautomation.model.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "User Registration, Login, and Email Verification Endpoints")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    @Operation(summary = "Register a new job seeker account")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(userService.registerUser(request));
    }

    @PostMapping("/login")
    @Operation(summary = "Authenticate candidate and retrieve JWT token")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        return ResponseEntity.ok(userService.loginUser(request));
    }

    @PostMapping("/verify-email")
    @Operation(summary = "Verify candidate email with 6-digit OTP code")
    public ResponseEntity<Map<String, Object>> verifyEmail(@Valid @RequestBody VerifyEmailRequest request) {
        User updatedUser = userService.verifyEmail(request.getEmail(), request.getCode());
        return ResponseEntity.ok(Map.of(
                "message", "Email verified successfully!",
                "emailVerified", updatedUser.isEmailVerified(),
                "user", updatedUser
        ));
    }

    @PostMapping("/resend-verification")
    @Operation(summary = "Resend verification code to candidate email")
    public ResponseEntity<Map<String, String>> resendVerification(@Valid @RequestBody ResendVerificationRequest request) {
        userService.resendVerificationCode(request.getEmail());
        return ResponseEntity.ok(Map.of(
                "message", "Verification code resent successfully to " + request.getEmail()
        ));
    }
}
