package com.jobautomation.repository;

import com.jobautomation.model.HRContact;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HRContactRepository extends MongoRepository<HRContact, String> {
    Optional<HRContact> findByEmail(String email);
    List<HRContact> findByCompanyName(String companyName);
}
