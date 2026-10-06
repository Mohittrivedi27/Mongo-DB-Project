package com.jobautomation.repository;

import com.jobautomation.model.EmailTemplate;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmailTemplateRepository extends MongoRepository<EmailTemplate, String> {
    List<EmailTemplate> findByUserId(String userId);
    Optional<EmailTemplate> findByUserIdAndIsDefaultTrue(String userId);
}
