package com.jobautomation.dto;

public class SettingsDto {
    private int dailyLimit;
    private int hourlyLimit;
    private String scheduleStartTime;
    private String scheduleEndTime;

    public SettingsDto() {}

    public SettingsDto(int dailyLimit, int hourlyLimit, String scheduleStartTime, String scheduleEndTime) {
        this.dailyLimit = dailyLimit;
        this.hourlyLimit = hourlyLimit;
        this.scheduleStartTime = scheduleStartTime;
        this.scheduleEndTime = scheduleEndTime;
    }

    public int getDailyLimit() { return dailyLimit; }
    public void setDailyLimit(int dailyLimit) { this.dailyLimit = dailyLimit; }

    public int getHourlyLimit() { return hourlyLimit; }
    public void setHourlyLimit(int hourlyLimit) { this.hourlyLimit = hourlyLimit; }

    public String getScheduleStartTime() { return scheduleStartTime; }
    public void setScheduleStartTime(String scheduleStartTime) { this.scheduleStartTime = scheduleStartTime; }

    public String getScheduleEndTime() { return scheduleEndTime; }
    public void setScheduleEndTime(String scheduleEndTime) { this.scheduleEndTime = scheduleEndTime; }
}
