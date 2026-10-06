package com.jobautomation.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "sync_records")
public class SyncRecord {

    @Id
    private String id;

    @Indexed
    private String userId;

    private String spreadsheetId;
    private String syncType; // IMPORT_JOBS, IMPORT_HR, EXPORT_APPLICATIONS
    private int recordsProcessed;
    private int recordsAdded;
    private int recordsFailed;
    private String status; // SUCCESS, PARTIAL_SUCCESS, FAILED
    private String errorMessage;
    private Instant timestamp = Instant.now();

    public SyncRecord() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getSpreadsheetId() { return spreadsheetId; }
    public void setSpreadsheetId(String spreadsheetId) { this.spreadsheetId = spreadsheetId; }

    public String getSyncType() { return syncType; }
    public void setSyncType(String syncType) { this.syncType = syncType; }

    public int getRecordsProcessed() { return recordsProcessed; }
    public void setRecordsProcessed(int recordsProcessed) { this.recordsProcessed = recordsProcessed; }

    public int getRecordsAdded() { return recordsAdded; }
    public void setRecordsAdded(int recordsAdded) { this.recordsAdded = recordsAdded; }

    public int getRecordsFailed() { return recordsFailed; }
    public void setRecordsFailed(int recordsFailed) { this.recordsFailed = recordsFailed; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }

    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }
}
