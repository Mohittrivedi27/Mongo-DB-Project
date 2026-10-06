package com.jobautomation.service;

import com.jobautomation.model.Job;
import com.jobautomation.model.JobMatch;
import com.jobautomation.model.User;
import com.jobautomation.repository.JobMatchRepository;
import com.jobautomation.repository.JobRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class JobMatchingServiceTest {

    @Mock
    private JobRepository jobRepository;

    @Mock
    private JobMatchRepository jobMatchRepository;

    @Mock
    private UserService userService;

    @Mock
    private ResumeService resumeService;

    @InjectMocks
    private JobMatchingService jobMatchingService;

    private User user;
    private Job job;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId("user123");
        user.setName("John Doe");
        user.setSkills(Arrays.asList("Java", "Spring Boot", "MongoDB", "REST API"));
        user.setPreferredRoles(Arrays.asList("Java Backend Developer"));

        job = new Job();
        job.setId("job123");
        job.setCompanyName("Tech Solutions");
        job.setJobTitle("Java Backend Developer");
        job.setRequiredSkills(Arrays.asList("Java", "Spring Boot", "MongoDB", "REST API"));
    }

    @Test
    void testCalculateMatchScore_HighMatch() {
        Set<String> userSkillsLower = new HashSet<>(Arrays.asList("java", "spring boot", "mongodb", "rest api"));

        JobMatch match = jobMatchingService.calculateMatchScore(user, userSkillsLower, job);

        assertNotNull(match);
        assertTrue(match.getMatchScore() >= 90.0, "Expected match score >= 90%, but got: " + match.getMatchScore());
        assertEquals(4, match.getMatchedSkills().size());
        assertEquals(0, match.getMissingSkills().size());
    }

    @Test
    void testCalculateMatchScore_PartialMatch() {
        Set<String> userSkillsLower = new HashSet<>(Arrays.asList("java"));

        JobMatch match = jobMatchingService.calculateMatchScore(user, userSkillsLower, job);

        assertNotNull(match);
        assertTrue(match.getMatchScore() < 90.0);
        assertEquals(1, match.getMatchedSkills().size());
        assertEquals(3, match.getMissingSkills().size());
    }
}
