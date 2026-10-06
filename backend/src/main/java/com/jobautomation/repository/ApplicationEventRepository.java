package com.jobautomation.repository;

import com.jobautomation.model.ApplicationEvent;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApplicationEventRepository extends MongoRepository<ApplicationEvent, String> {
    List<ApplicationEvent> findByApplicationIdOrderByTimestampAsc(String applicationId);
}
