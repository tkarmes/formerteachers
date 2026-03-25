package com.formerteachers.controller;

import com.formerteachers.dto.EmployerSignupDto;
import com.formerteachers.model.EmployerProfile;
import com.formerteachers.model.User;
import com.formerteachers.repository.EmployerProfileRepository;
import com.formerteachers.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/employer-signup")
public class EmployerSignupController {

    private final UserRepository userRepository;
    private final EmployerProfileRepository employerProfileRepository;
    private final PasswordEncoder passwordEncoder;

    public EmployerSignupController(UserRepository userRepository, 
                                    EmployerProfileRepository employerProfileRepository, 
                                    PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.employerProfileRepository = employerProfileRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public String showSignupForm(Model model) {
        model.addAttribute("employerDto", new EmployerSignupDto());
        return "employer-signup";
    }

    @PostMapping
    public String processSignup(@Valid @ModelAttribute("employerDto") EmployerSignupDto dto, 
                                BindingResult result, Model model) {
        
        if (userRepository.findByUsername(dto.getUsername()).isPresent()) {
            result.rejectValue("username", "error.user", "Email already exists");
        }

        if (result.hasErrors()) {
            return "employer-signup";
        }

        // 1. Create User
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole("EMPLOYER");
        userRepository.save(user);

        // 2. Create Employer Profile
        EmployerProfile profile = new EmployerProfile();
        profile.setUser(user);
        profile.setCompanyName(dto.getCompanyName());
        profile.setCompanyDescription(dto.getCompanyDescription());
        profile.setWebsite(dto.getWebsite());
        employerProfileRepository.save(profile);

        return "redirect:/login?employerRegistered=true";
    }
}
