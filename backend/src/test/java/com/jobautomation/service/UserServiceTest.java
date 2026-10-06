package com.jobautomation.service;

import com.jobautomation.dto.AuthResponse;
import com.jobautomation.dto.RegisterRequest;
import com.jobautomation.model.User;
import com.jobautomation.repository.UserRepository;
import com.jobautomation.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider tokenProvider;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private UserService userService;

    private User sampleUser;

    @BeforeEach
    void setUp() {
        sampleUser = new User("Alice Smith", "alice@example.com", "encodedPassword");
        sampleUser.setId("user999");
        sampleUser.setEmailVerified(false);
        sampleUser.setVerificationToken("654321");
        sampleUser.setVerificationTokenExpiry(Instant.now().plusSeconds(3600));
    }

    @Test
    void testRegisterUser_SetsVerificationTokenAndSendsEmail() {
        RegisterRequest req = new RegisterRequest();
        req.setName("Bob Jones");
        req.setEmail("bob@example.com");
        req.setPassword("secret123");

        when(userRepository.existsByEmail("bob@example.com")).thenReturn(false);
        when(passwordEncoder.encode("secret123")).thenReturn("hashedSecret");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(tokenProvider.generateTokenFromEmail("bob@example.com")).thenReturn("mock-jwt-token");

        AuthResponse resp = userService.registerUser(req);

        assertNotNull(resp);
        assertEquals("mock-jwt-token", resp.getToken());
        assertFalse(resp.getUser().isEmailVerified());
        assertNotNull(resp.getUser().getVerificationToken());
        verify(emailService, times(1)).sendVerificationEmail(eq("bob@example.com"), anyString());
    }

    @Test
    void testVerifyEmail_Success() {
        when(userRepository.findByEmail("alice@example.com")).thenReturn(Optional.of(sampleUser));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User verifiedUser = userService.verifyEmail("alice@example.com", "654321");

        assertTrue(verifiedUser.isEmailVerified());
        assertNull(verifiedUser.getVerificationToken());
        assertNull(verifiedUser.getVerificationTokenExpiry());
        verify(userRepository, times(1)).save(sampleUser);
    }

    @Test
    void testVerifyEmail_InvalidCode() {
        when(userRepository.findByEmail("alice@example.com")).thenReturn(Optional.of(sampleUser));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                userService.verifyEmail("alice@example.com", "000000"));

        assertTrue(ex.getMessage().contains("Invalid verification code"));
        assertFalse(sampleUser.isEmailVerified());
    }

    @Test
    void testResendVerificationCode_Success() {
        when(userRepository.findByEmail("alice@example.com")).thenReturn(Optional.of(sampleUser));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        userService.resendVerificationCode("alice@example.com");

        assertNotNull(sampleUser.getVerificationToken());
        verify(emailService, times(1)).sendVerificationEmail(eq("alice@example.com"), eq(sampleUser.getVerificationToken()));
    }
}
