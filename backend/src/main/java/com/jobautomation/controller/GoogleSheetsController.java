package com.jobautomation.controller;

import com.jobautomation.dto.GoogleSheetsImportRequest;
import com.jobautomation.model.SyncRecord;
import com.jobautomation.service.GoogleSheetsSyncService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/google-sheets")
@Tag(name = "Google Sheets Sync", description = "Google Sheets API import & synchronization endpoints")
@SecurityRequirement(name = "bearerAuth")
public class GoogleSheetsController {

    private final GoogleSheetsSyncService googleSheetsSyncService;

    public GoogleSheetsController(GoogleSheetsSyncService googleSheetsSyncService) {
        this.googleSheetsSyncService = googleSheetsSyncService;
    }

    @PostMapping("/import-jobs")
    @Operation(summary = "Import job postings and recruiter contacts from Google Sheets")
    public ResponseEntity<SyncRecord> importJobs(@AuthenticationPrincipal UserDetails userDetails,
                                                 @Valid @RequestBody GoogleSheetsImportRequest request) {
        return ResponseEntity.ok(googleSheetsSyncService.importJobsFromSpreadsheet(getUserEmail(userDetails), request.getSpreadsheetId()));
    }

    @GetMapping("/records")
    @Operation(summary = "Get sync record history")
    public ResponseEntity<List<SyncRecord>> getSyncRecords(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(googleSheetsSyncService.getUserSyncRecords(getUserEmail(userDetails)));
    }

    private String getUserEmail(UserDetails userDetails) {
        if (userDetails != null) return userDetails.getUsername();
        return "demo@jobautomation.com";
    }
}
