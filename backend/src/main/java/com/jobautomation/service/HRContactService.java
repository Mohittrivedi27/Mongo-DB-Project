package com.jobautomation.service;

import com.jobautomation.exception.ResourceNotFoundException;
import com.jobautomation.model.HRContact;
import com.jobautomation.repository.HRContactRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class HRContactService {

    private final HRContactRepository hrContactRepository;

    public HRContactService(HRContactRepository hrContactRepository) {
        this.hrContactRepository = hrContactRepository;
    }

    public HRContact createContact(HRContact contact) {
        Optional<HRContact> existing = hrContactRepository.findByEmail(contact.getEmail());
        if (existing.isPresent()) {
            return existing.get();
        }
        contact.setCreatedAt(Instant.now());
        if (contact.getVerificationStatus() == null) {
            contact.setVerificationStatus("VERIFIED");
        }
        return hrContactRepository.save(contact);
    }

    public List<HRContact> getAllContacts() {
        return hrContactRepository.findAll();
    }

    public HRContact getContactById(String id) {
        return hrContactRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("HR Contact not found with ID: " + id));
    }

    public Optional<HRContact> getContactByEmail(String email) {
        return hrContactRepository.findByEmail(email);
    }

    public List<HRContact> getContactsByCompany(String companyName) {
        return hrContactRepository.findByCompanyName(companyName);
    }
}
