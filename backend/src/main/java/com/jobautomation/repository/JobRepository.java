package com.jobautomation.repository;

import com.jobautomation.model.Job;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JobRepository extends MongoRepository<Job, String> {
    List<Job> findByStatus(String status);
    Optional<Job> findByCompanyNameAndJobTitle(String companyName, String jobTitle);
}
