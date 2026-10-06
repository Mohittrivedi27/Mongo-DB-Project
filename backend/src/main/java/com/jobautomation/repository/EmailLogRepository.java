package com.jobautomation.repository;

import com.jobautomation.model.EmailLog;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmailLogRepository extends MongoRepository<EmailLog, String> {
    List<EmailLog> findByUserId(String userId);
    List<EmailLog> findByApplicationId(String applicationId);
}
