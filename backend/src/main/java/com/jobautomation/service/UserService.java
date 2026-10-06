package com.jobautomation.service;

import com.jobautomation.dto.AuthRequest;
import com.jobautomation.dto.AuthResponse;
import com.jobautomation.dto.RegisterRequest;
import com.jobautomation.dto.SettingsDto;

import com.jobautomation.exception.ResourceNotFoundException;
import com.jobautomation.model.User;
import com.jobautomation.repository.UserRepository;
import com.jobautomation.security.JwtTokenProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       JwtTokenProvider tokenProvider, AuthenticationManager authenticationManager,
                       EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
        this.authenticationManager = authenticationManager;
        this.emailService = emailService;
    }

    public AuthResponse registerUser(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email address is already registered!");
        }

        User user = new User(
                request.getName(),
                request.getEmail(),
                passwordEncoder.encode(request.getPassword())
        );
        user.setPhone(request.getPhone());
        user.setLocation(request.getLocation());

        String verificationCode = String.format("%06d", new java.util.Random().nextInt(900000) + 100000);
        user.setEmailVerified(false);
        user.setVerificationToken(verificationCode);
        user.setVerificationTokenExpiry(Instant.now().plus(24, java.time.temporal.ChronoUnit.HOURS));

        User savedUser = userRepository.save(user);
        String token = tokenProvider.generateTokenFromEmail(savedUser.getEmail());

        emailService.sendVerificationEmail(savedUser.getEmail(), verificationCode);

        return new AuthResponse(token, savedUser);
    }

    public AuthResponse loginUser(AuthRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        String token = tokenProvider.generateToken(authentication);
        return new AuthResponse(token, user);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }

    public User updateProfile(String email, User updatedUser) {
        User user = getUserByEmail(email);
        user.setName(updatedUser.getName());
        user.setPhone(updatedUser.getPhone());
        user.setLocation(updatedUser.getLocation());
        user.setPortfolioUrl(updatedUser.getPortfolioUrl());
        user.setGithubUrl(updatedUser.getGithubUrl());
        user.setLinkedinUrl(updatedUser.getLinkedinUrl());
        user.setUpdatedAt(Instant.now());
        return userRepository.save(user);
    }

    public User updateSkills(String email, List<String> skills) {
        User user = getUserByEmail(email);
        user.setSkills(skills);
        user.setUpdatedAt(Instant.now());
        return userRepository.save(user);
    }

    public User updatePreferredRoles(String email, List<String> preferredRoles) {
        User user = getUserByEmail(email);
        user.setPreferredRoles(preferredRoles);
        user.setUpdatedAt(Instant.now());
        return userRepository.save(user);
    }

    public SettingsDto getSettings(String email) {
        User user = getUserByEmail(email);
        return new SettingsDto(user.getDailyLimit(), user.getHourlyLimit(), user.getScheduleStartTime(), user.getScheduleEndTime());
    }

    public SettingsDto updateSettings(String email, SettingsDto dto) {
        User user = getUserByEmail(email);
        user.setDailyLimit(dto.getDailyLimit());
        user.setHourlyLimit(dto.getHourlyLimit());
        user.setScheduleStartTime(dto.getScheduleStartTime());
        user.setScheduleEndTime(dto.getScheduleEndTime());
        user.setUpdatedAt(Instant.now());
        userRepository.save(user);
        return dto;
    }

    public User verifyEmail(String email, String code) {
        User user = getUserByEmail(email);
        if (user.isEmailVerified()) {
            return user;
        }
        if (user.getVerificationToken() == null || !user.getVerificationToken().equals(code.trim())) {
            throw new IllegalArgumentException("Invalid verification code. Please check and try again.");
        }
        if (user.getVerificationTokenExpiry() != null && Instant.now().isAfter(user.getVerificationTokenExpiry())) {
            throw new IllegalArgumentException("Verification code has expired. Please request a new code.");
        }

        user.setEmailVerified(true);
        user.setVerificationToken(null);
        user.setVerificationTokenExpiry(null);
        user.setUpdatedAt(Instant.now());
        return userRepository.save(user);
    }

    public void resendVerificationCode(String email) {
        User user = getUserByEmail(email);
        if (user.isEmailVerified()) {
            throw new IllegalArgumentException("Email is already verified.");
        }

        String verificationCode = String.format("%06d", new java.util.Random().nextInt(900000) + 100000);
        user.setVerificationToken(verificationCode);
        user.setVerificationTokenExpiry(Instant.now().plus(24, java.time.temporal.ChronoUnit.HOURS));
        user.setUpdatedAt(Instant.now());
        userRepository.save(user);

        emailService.sendVerificationEmail(user.getEmail(), verificationCode);
    }
}
