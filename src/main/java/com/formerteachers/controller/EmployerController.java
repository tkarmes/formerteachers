package com.formerteachers.controller;

import com.formerteachers.model.EmployerProfile;
import com.formerteachers.model.Teacher;
import com.formerteachers.model.User;
import com.formerteachers.repository.EmployerProfileRepository;
import com.formerteachers.repository.TeacherRepository;
import com.formerteachers.repository.UserRepository;
import com.formerteachers.service.TeacherService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Controller
@RequestMapping("/employer")
public class EmployerController {

    private static final Logger logger = LoggerFactory.getLogger(EmployerController.class);
    private final EmployerProfileRepository employerProfileRepository;
    private final UserRepository userRepository;
    private final TeacherService teacherService;
    private final TeacherRepository teacherRepository;

    // Define the upload directory relative to the project root
    private static final String UPLOAD_DIR = "src/main/resources/static/images/logos/";

    @Autowired
    public EmployerController(EmployerProfileRepository employerProfileRepository, 
                               UserRepository userRepository,
                               TeacherService teacherService,
                               TeacherRepository teacherRepository) {
        this.employerProfileRepository = employerProfileRepository;
        this.userRepository = userRepository;
        this.teacherService = teacherService;
        this.teacherRepository = teacherRepository;
    }

    @GetMapping("/profile/edit")
    public String showEditProfileForm(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User user = userRepository.findByUsername(username).orElseThrow();
        
        EmployerProfile profile = employerProfileRepository.findByUser(user)
                .orElse(new EmployerProfile(user, "New Company"));
        
        logger.info("Showing edit profile form for user: {}. Profile logo URL: {}", username, profile.getLogoUrl());
        
        model.addAttribute("profile", profile);
        return "edit-employer-profile";
    }

    @PostMapping("/profile/edit")
    public String updateProfile(@ModelAttribute EmployerProfile updatedProfile, 
                                @RequestParam(value = "logoFile", required = false) MultipartFile logoFile) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User user = userRepository.findByUsername(username).orElseThrow();
        
        EmployerProfile existingProfile = employerProfileRepository.findByUser(user)
                .orElse(new EmployerProfile(user, updatedProfile.getCompanyName()));
        
        logger.info("Updating profile for user: {}. New company name: {}", username, updatedProfile.getCompanyName());
        
        existingProfile.setCompanyName(updatedProfile.getCompanyName());
        existingProfile.setCompanyDescription(updatedProfile.getCompanyDescription());
        existingProfile.setWebsite(updatedProfile.getWebsite());

        // Handle logo file upload
        if (logoFile != null && !logoFile.isEmpty()) {
            try {
                // Ensure the upload directory exists
                Path uploadPath = Paths.get(UPLOAD_DIR);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                // Generate a unique file name
                String originalFilename = logoFile.getOriginalFilename();
                String fileExtension = "";
                if (originalFilename != null && originalFilename.contains(".")) {
                    fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
                }
                String uniqueFileName = UUID.randomUUID().toString() + fileExtension;
                Path filePath = uploadPath.resolve(uniqueFileName);
                
                // Save the file
                Files.copy(logoFile.getInputStream(), filePath);
                
                // Set the logoUrl to the relative path for web access
                existingProfile.setLogoUrl("/images/logos/" + uniqueFileName);
                logger.info("New logo uploaded for user {}: {}", username, existingProfile.getLogoUrl());

            } catch (IOException e) {
                logger.error("Failed to upload logo for user {}: {}", username, e.getMessage());
                // Optionally, add an error message to the model or redirect with an error param
            }
        } else {
            // If no new file is uploaded, retain the existing logoUrl
            // The @ModelAttribute already binds the existing logoUrl if it was present in the form
            // but since we are removing the text input, we need to explicitly keep it if no file is uploaded.
            // However, if the form doesn't send the logoUrl anymore, we need to fetch it from the existing profile.
            // For now, we assume if logoFile is null/empty, we don't change the logoUrl.
            // If the user wants to remove the logo, a separate mechanism would be needed.
            logger.info("No new logo file uploaded for user {}. Retaining existing logo URL.", username);
        }
        
        employerProfileRepository.save(existingProfile);
        return "redirect:/jobs/dashboard?profileUpdated=true";
    }

    @GetMapping("/browse-talent")
    public String browseTalent(Model model) {
        model.addAttribute("teachers", teacherService.getPublicTeachers());
        return "browse-talent";
    }

    @GetMapping("/teacher-profile/{id}")
    public String viewTeacherProfile(@PathVariable Long id, Model model) {
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));
        
        // Safety check: only allow viewing if profile is public
        if (!teacher.isPublicProfile()) {
            return "redirect:/employer/browse-talent?error=private";
        }
        
        model.addAttribute("teacher", teacher);
        return "teacher-profile-view";
    }
}
