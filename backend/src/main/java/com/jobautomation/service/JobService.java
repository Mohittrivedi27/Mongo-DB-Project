package com.jobautomation.service;

import com.jobautomation.exception.ResourceNotFoundException;
import com.jobautomation.model.Job;
import com.jobautomation.repository.JobRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class JobService {

    private final JobRepository jobRepository;

    public JobService(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    public Job createJob(Job job) {
        if (job.getPostedDate() == null) {
            job.setPostedDate(Instant.now());
        }
        job.setCreatedAt(Instant.now());
        job.setUpdatedAt(Instant.now());
        return jobRepository.save(job);
    }

    public List<Job> getAllJobs() {
        return jobRepository.findAll();
    }

    public Page<Job> getJobsPaginated(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return jobRepository.findAll(pageable);
    }

    public Job getJobById(String id) {
        return jobRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with ID: " + id));
    }

    public Job updateJob(String id, Job updatedJob) {
        Job existing = getJobById(id);
        existing.setCompanyName(updatedJob.getCompanyName());
        existing.setJobTitle(updatedJob.getJobTitle());
        existing.setDescription(updatedJob.getDescription());
        existing.setRequiredSkills(updatedJob.getRequiredSkills());
        existing.setPreferredSkills(updatedJob.getPreferredSkills());
        existing.setLocation(updatedJob.getLocation());
        existing.setWorkMode(updatedJob.getWorkMode());
        existing.setExperienceRequired(updatedJob.getExperienceRequired());
        existing.setSalary(updatedJob.getSalary());
        existing.setJobUrl(updatedJob.getJobUrl());
        existing.setStatus(updatedJob.getStatus());
        existing.setUpdatedAt(Instant.now());
        return jobRepository.save(existing);
    }

    public void deleteJob(String id) {
        if (!jobRepository.existsById(id)) {
            throw new ResourceNotFoundException("Job not found with ID: " + id);
        }
        jobRepository.deleteById(id);
    }

    public Optional<Job> findByCompanyAndTitle(String company, String title) {
        return jobRepository.findByCompanyNameAndJobTitle(company, title);
    }
}
