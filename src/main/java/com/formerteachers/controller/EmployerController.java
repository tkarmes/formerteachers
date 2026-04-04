package com.formerteachers.controller;

import com.formerteachers.model.EmployerProfile;
import com.formerteachers.model.User;
import com.formerteachers.repository.EmployerProfileRepository;
import com.formerteachers.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/employer/profile")
public class EmployerController {

    private static final Logger logger = LoggerFactory.getLogger(EmployerController.class);
    private final EmployerProfileRepository employerProfileRepository;
    private final UserRepository userRepository;

    @Autowired
    public EmployerController(EmployerProfileRepository employerProfileRepository, UserRepository userRepository) {
        this.employerProfileRepository = employerProfileRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/edit")
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

    @PostMapping("/edit")
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
}
