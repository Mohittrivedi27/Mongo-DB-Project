package com.jobautomation.service;

import com.jobautomation.exception.ResourceNotFoundException;
import com.jobautomation.model.Resume;
import com.jobautomation.model.User;
import com.jobautomation.repository.ResumeRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ResumeService {

    private final ResumeRepository resumeRepository;
    private final UserService userService;
    private final Path storageLocation = Paths.get("uploads/resumes").toAbsolutePath().normalize();

    public ResumeService(ResumeRepository resumeRepository, UserService userService) {
        this.resumeRepository = resumeRepository;
        this.userService = userService;
        try {
            Files.createDirectories(this.storageLocation);
        } catch (IOException e) {
            throw new RuntimeException("Could not create storage directory for resumes", e);
        }
    }

    public Resume uploadResume(String email, String name, String type, String associatedRolesStr, MultipartFile file) throws IOException {
        User user = userService.getUserByEmail(email);

        String originalFilename = file.getOriginalFilename();
        String fileExtension = originalFilename != null && originalFilename.contains(".") ?
                originalFilename.substring(originalFilename.lastIndexOf(".")) : ".pdf";
        String storedFileName = UUID.randomUUID().toString() + fileExtension;
        Path targetPath = this.storageLocation.resolve(storedFileName);

        Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

        List<String> rolesList = Arrays.stream(associatedRolesStr.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());

        Resume resume = new Resume();
        resume.setUserId(user.getId());
        resume.setName(name);
        resume.setType(type);
        resume.setAssociatedRoles(rolesList);
        resume.setFileName(originalFilename);
        resume.setFileStorageReference(targetPath.toString());
        resume.setContentType(file.getContentType());
        resume.setFileSize(file.getSize());
        resume.setActive(true);
        resume.setCreatedAt(Instant.now());

        return resumeRepository.save(resume);
    }

    public List<Resume> getUserResumes(String email) {
        User user = userService.getUserByEmail(email);
        return resumeRepository.findByUserIdAndActiveTrue(user.getId());
    }

    public void deleteResume(String email, String resumeId) {
        User user = userService.getUserByEmail(email);
        Resume resume = resumeRepository.findByIdAndUserId(resumeId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Resume not found with ID: " + resumeId));

        resume.setActive(false);
        resumeRepository.save(resume);
    }

    public Resume selectAppropriateResume(String userId, String jobTitle) {
        List<Resume> userResumes = resumeRepository.findByUserIdAndActiveTrue(userId);
        if (userResumes.isEmpty()) {
            return null;
        }

        String lowerTitle = jobTitle.toLowerCase();
        for (Resume resume : userResumes) {
            if (resume.getType() != null && lowerTitle.contains(resume.getType().toLowerCase())) {
                return resume;
            }
            if (resume.getAssociatedRoles() != null) {
                for (String role : resume.getAssociatedRoles()) {
                    if (lowerTitle.contains(role.toLowerCase())) {
                        return resume;
                    }
                }
            }
        }

        return userResumes.get(0); // Fallback to primary resume if available
    }
}
