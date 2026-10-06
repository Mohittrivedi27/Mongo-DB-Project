package com.jobautomation.dto;

import java.util.ArrayList;
import java.util.List;

public class SkillsUpdateRequest {
    private List<String> skills = new ArrayList<>();

    public SkillsUpdateRequest() {}

    public List<String> getSkills() { return skills; }
    public void setSkills(List<String> skills) { this.skills = skills; }
}
