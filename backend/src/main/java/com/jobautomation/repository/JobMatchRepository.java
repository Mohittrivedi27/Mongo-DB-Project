package com.jobautomation.repository;

import com.jobautomation.model.JobMatch;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JobMatchRepository extends MongoRepository<JobMatch, String> {
    List<JobMatch> findByUserIdOrderByMatchScoreDesc(String userId);
    Optional<JobMatch> findByUserIdAndJobId(String userId, String jobId);
    void deleteByUserId(String userId);
}
