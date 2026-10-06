package com.jobautomation.repository;

import com.jobautomation.model.SyncRecord;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SyncRecordRepository extends MongoRepository<SyncRecord, String> {
    List<SyncRecord> findByUserIdOrderByTimestampDesc(String userId);
}
