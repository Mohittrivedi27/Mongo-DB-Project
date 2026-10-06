package com.jobautomation.controller;

import com.jobautomation.model.Application;
import com.jobautomation.model.User;
import com.jobautomation.repository.ApplicationRepository;
import com.jobautomation.repository.JobRepository;
import com.jobautomation.service.JobMatchingService;
import com.jobautomation.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@Tag(name = "Dashboard", description = "Dashboard analytics and metrics endpoints")
@SecurityRequirement(name = "bearerAuth")
public class DashboardController {

    private final JobRepository jobRepository;
    private final ApplicationRepository applicationRepository;
    private final UserService userService;
    private final JobMatchingService jobMatchingService;

    public DashboardController(JobRepository jobRepository, ApplicationRepository applicationRepository,
                               UserService userService, JobMatchingService jobMatchingService) {
        this.jobRepository = jobRepository;
        this.applicationRepository = applicationRepository;
        this.userService = userService;
        this.jobMatchingService = jobMatchingService;
    }

    @GetMapping("/stats")
    @Operation(summary = "Get aggregated dashboard statistics for candidate")
    public ResponseEntity<Map<String, Object>> getDashboardStats(@AuthenticationPrincipal UserDetails userDetails) {
        String email = getUserEmail(userDetails);
        User user = userService.getUserByEmail(email);

        List<Application> apps = applicationRepository.findByUserId(user.getId());
        Instant startOfDay = Instant.now().truncatedTo(ChronoUnit.DAYS);

        long appsToday = applicationRepository.countByUserIdAndCreatedAtGreaterThanEqual(user.getId(), startOfDay);
        long matchingJobs = jobMatchingService.getUserMatches(email).stream().filter(m -> m.getMatchScore() >= 70).count();

        long sentCount = apps.stream().filter(a -> "SENT".equals(a.getStatus()) || "DELIVERED".equals(a.getStatus())).count();
        long deliveredCount = apps.stream().filter(a -> "DELIVERED".equals(a.getStatus()) || "SENT".equals(a.getStatus())).count();
        long repliesCount = apps.stream().filter(a -> "REPLIED".equals(a.getStatus())).count();
        long interviewsCount = apps.stream().filter(a -> "INTERVIEW".equals(a.getStatus())).count();
        long rejectedCount = apps.stream().filter(a -> "REJECTED".equals(a.getStatus())).count();
        long offersCount = apps.stream().filter(a -> "OFFER".equals(a.getStatus())).count();

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalJobs", jobRepository.count());
        stats.put("matchingJobs", matchingJobs);
        stats.put("applicationsToday", appsToday);
        stats.put("dailyLimit", user.getDailyLimit());
        stats.put("hourlyLimit", user.getHourlyLimit());
        stats.put("applicationsSent", sentCount);
        stats.put("emailsDelivered", deliveredCount);
        stats.put("replies", repliesCount);
        stats.put("interviews", interviewsCount);
        stats.put("rejected", rejectedCount);
        stats.put("offers", offersCount);

        return ResponseEntity.ok(stats);
    }

    private String getUserEmail(UserDetails userDetails) {
        if (userDetails != null) return userDetails.getUsername();
        return "demo@jobautomation.com";
    }
}
