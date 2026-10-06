package com.jobautomation.controller;

import com.jobautomation.model.HRContact;
import com.jobautomation.service.HRContactService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hr-contacts")
@Tag(name = "HR Contacts", description = "Recruiter & HR contact directory endpoints")
@SecurityRequirement(name = "bearerAuth")
public class HRContactController {

    private final HRContactService hrContactService;

    public HRContactController(HRContactService hrContactService) {
        this.hrContactService = hrContactService;
    }

    @GetMapping
    @Operation(summary = "Get all HR contacts")
    public ResponseEntity<List<HRContact>> getAllContacts() {
        return ResponseEntity.ok(hrContactService.getAllContacts());
    }

    @PostMapping
    @Operation(summary = "Create a new HR contact")
    public ResponseEntity<HRContact> createContact(@RequestBody HRContact contact) {
        return ResponseEntity.ok(hrContactService.createContact(contact));
    }
}
