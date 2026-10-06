package com.jobautomation.dto;

import java.util.ArrayList;
import java.util.List;

public class RolesUpdateRequest {
    private List<String> preferredRoles = new ArrayList<>();

    public RolesUpdateRequest() {}

    public List<String> getPreferredRoles() { return preferredRoles; }
    public void setPreferredRoles(List<String> preferredRoles) { this.preferredRoles = preferredRoles; }
}
