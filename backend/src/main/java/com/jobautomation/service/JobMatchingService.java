package com.jobautomation.service;

import com.jobautomation.model.Job;
import com.jobautomation.model.JobMatch;
import com.jobautomation.model.Resume;
import com.jobautomation.model.User;
import com.jobautomation.repository.JobMatchRepository;
import com.jobautomation.repository.JobRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class JobMatchingService {

    private final JobRepository jobRepository;
    private final JobMatchRepository jobMatchRepository;
    private final UserService userService;
    private final ResumeService resumeService;

    public JobMatchingService(JobRepository jobRepository, JobMatchRepository jobMatchRepository,
                              UserService userService, ResumeService resumeService) {
        this.jobRepository = jobRepository;
        this.jobMatchRepository = jobMatchRepository;
        this.userService = userService;
        this.resumeService = resumeService;
    }

    public List<JobMatch> calculateAndSaveMatchesForUser(String email) {
        User user = userService.getUserByEmail(email);
        List<Job> openJobs = jobRepository.findByStatus("OPEN");
        if (openJobs.isEmpty()) {
            openJobs = jobRepository.findAll();
        }

        List<JobMatch> matches = new ArrayList<>();
        Set<String> userSkillsLower = user.getSkills().stream()
                .map(String::toLowerCase)
                .collect(Collectors.toSet());

        for (Job job : openJobs) {
            JobMatch match = calculateMatchScore(user, userSkillsLower, job);
            jobMatchRepository.findByUserIdAndJobId(user.getId(), job.getId())
                    .ifPresent(existing -> match.setId(existing.getId()));

            matches.add(jobMatchRepository.save(match));
        }

        return jobMatchRepository.findByUserIdOrderByMatchScoreDesc(user.getId());
    }

    public JobMatch calculateMatchScore(User user, Set<String> userSkillsLower, Job job) {
        List<String> required = job.getRequiredSkills() != null ? job.getRequiredSkills() : new ArrayList<>();
        List<String> matchedSkills = new ArrayList<>();
        List<String> missingSkills = new ArrayList<>();

        int matchedCount = 0;
        for (String reqSkill : required) {
            if (userSkillsLower.contains(reqSkill.toLowerCase())) {
                matchedCount++;
                matchedSkills.add(reqSkill);
            } else {
                missingSkills.add(reqSkill);
            }
        }

        double skillScore = required.isEmpty() ? 70.0 : ((double) matchedCount / required.size()) * 100.0;

        // Role score check
        double roleScore = 50.0;
        if (user.getPreferredRoles() != null && job.getJobTitle() != null) {
            String lowerTitle = job.getJobTitle().toLowerCase();
            for (String prefRole : user.getPreferredRoles()) {
                if (lowerTitle.contains(prefRole.toLowerCase()) || prefRole.toLowerCase().contains(lowerTitle)) {
                    roleScore = 100.0;
                    break;
                }
            }
        }

        // Weighted Score (60% Skills + 40% Role)
        double totalScore = Math.round((skillScore * 0.60) + (roleScore * 0.40));
        if (totalScore > 100.0) totalScore = 100.0;

        Resume recommendedResume = resumeService.selectAppropriateResume(user.getId(), job.getJobTitle());

        JobMatch match = new JobMatch();
        match.setUserId(user.getId());
        match.setJobId(job.getId());
        match.setMatchScore(totalScore);
        match.setMatchedSkills(matchedSkills);
        match.setMissingSkills(missingSkills);
        match.setRecommendedResumeId(recommendedResume != null ? recommendedResume.getId() : null);
        match.setCalculatedAt(Instant.now());

        return match;
    }

    public List<JobMatch> getUserMatches(String email) {
        User user = userService.getUserByEmail(email);
        List<JobMatch> existing = jobMatchRepository.findByUserIdOrderByMatchScoreDesc(user.getId());
        if (existing.isEmpty()) {
            return calculateAndSaveMatchesForUser(email);
        }
        return existing;
    }
}
