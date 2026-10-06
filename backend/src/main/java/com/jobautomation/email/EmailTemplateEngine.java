package com.jobautomation.email;

import com.jobautomation.model.User;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EmailTemplateEngine {

    public String render(String templateText, User user, String hrName, String companyName, String jobTitle) {
        if (templateText == null) return "";

        String candidateName = user.getName() != null ? user.getName() : "Job Candidate";
        String skillsStr = user.getSkills() != null && !user.getSkills().isEmpty() ?
                String.join(", ", user.getSkills()) : "Java, Spring Boot, REST APIs, MongoDB";
        String portfolio = user.getPortfolioUrl() != null ? user.getPortfolioUrl() : "";
        String github = user.getGithubUrl() != null ? user.getGithubUrl() : "";
        String linkedin = user.getLinkedinUrl() != null ? user.getLinkedinUrl() : "";
        String actualHrName = hrName != null ? hrName : "Hiring Manager";

        return templateText
                .replace("{{candidateName}}", candidateName)
                .replace("{{hrName}}", actualHrName)
                .replace("{{companyName}}", companyName != null ? companyName : "Company")
                .replace("{{jobTitle}}", jobTitle != null ? jobTitle : "Position")
                .replace("{{skills}}", skillsStr)
                .replace("{{portfolio}}", portfolio)
                .replace("{{github}}", github)
                .replace("{{linkedin}}", linkedin);
    }
}
