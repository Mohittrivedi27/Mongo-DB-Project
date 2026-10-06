package com.jobautomation.dto;

import jakarta.validation.constraints.NotBlank;

public class GoogleSheetsImportRequest {

    @NotBlank(message = "Spreadsheet ID is required")
    private String spreadsheetId;

    public GoogleSheetsImportRequest() {}

    public GoogleSheetsImportRequest(String spreadsheetId) {
        this.spreadsheetId = spreadsheetId;
    }

    public String getSpreadsheetId() { return spreadsheetId; }
    public void setSpreadsheetId(String spreadsheetId) { this.spreadsheetId = spreadsheetId; }
}
