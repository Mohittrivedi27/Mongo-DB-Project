package com.jobautomation.repository;

import com.jobautomation.model.Application;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationRepository extends MongoRepository<Application, String> {
    List<Application> findByUserId(String userId);
    Optional<Application> findByIdAndUserId(String id, String userId);
    List<Application> findByUserIdAndStatus(String userId, String status);
    List<Application> findByStatus(String status);
    boolean existsByUserIdAndJobId(String userId, String jobId);
    long countByUserIdAndCreatedAtGreaterThanEqual(String userId, Instant timestamp);
}
