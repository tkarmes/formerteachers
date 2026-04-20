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

@Controller
@RequestMapping("/employer")
public class EmployerController {

    private static final Logger logger = LoggerFactory.getLogger(EmployerController.class);
    private final EmployerProfileRepository employerProfileRepository;
    private final UserRepository userRepository;
    private final TeacherService teacherService;
    private final TeacherRepository teacherRepository;

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
    public String updateProfile(@ModelAttribute EmployerProfile updatedProfile) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User user = userRepository.findByUsername(username).orElseThrow();
        
        EmployerProfile existingProfile = employerProfileRepository.findByUser(user)
                .orElse(new EmployerProfile(user, updatedProfile.getCompanyName()));
        
        logger.info("Updating profile for user: {}. New logo URL: {}", username, updatedProfile.getLogoUrl());
        
        existingProfile.setCompanyName(updatedProfile.getCompanyName());
        existingProfile.setCompanyDescription(updatedProfile.getCompanyDescription());
        existingProfile.setWebsite(updatedProfile.getWebsite());
        existingProfile.setLogoUrl(updatedProfile.getLogoUrl());
        
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
