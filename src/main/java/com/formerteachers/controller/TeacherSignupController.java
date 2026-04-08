package com.formerteachers.controller;

import com.formerteachers.dto.TeacherSignupDto;
import com.formerteachers.model.Teacher;
import com.formerteachers.model.User;
import com.formerteachers.repository.TeacherRepository;
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
@RequestMapping("/teacher-signup")
public class TeacherSignupController {

    private final UserRepository userRepository;
    private final TeacherRepository teacherRepository;
    private final PasswordEncoder passwordEncoder;

    public TeacherSignupController(UserRepository userRepository,
                                   TeacherRepository teacherRepository,
                                   PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.teacherRepository = teacherRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public String showSignupForm(Model model) {
        model.addAttribute("teacherDto", new TeacherSignupDto());
        return "teacher-signup";
    }

    @PostMapping
    public String processSignup(@Valid @ModelAttribute("teacherDto") TeacherSignupDto dto,
                                BindingResult result, Model model) {

        if (userRepository.findByUsername(dto.getUsername()).isPresent()) {
            result.rejectValue("username", "error.user", "Email already exists");
        }

        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            result.rejectValue("confirmPassword", "error.password", "Passwords do not match");
        }

        if (result.hasErrors()) {
            return "teacher-signup";
        }

        // 1. Create User
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole("TEACHER");
        userRepository.save(user);

        // 2. Create Teacher Profile
        Teacher teacher = new Teacher();
        teacher.setUser(user);
        teacherRepository.save(teacher);

        return "redirect:/login?teacherRegistered=true";
    }
}
