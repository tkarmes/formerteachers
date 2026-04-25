package com.formerteachers.controller;

import com.formerteachers.model.Job;
import com.formerteachers.model.Teacher;
import com.formerteachers.model.User;
import com.formerteachers.service.TeacherService;
import com.formerteachers.repository.UserRepository;
import com.formerteachers.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.Set;

@Controller
@RequestMapping("/teacher")
public class TeacherController {

    @Autowired
    private TeacherService teacherService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FileService fileService;

    @GetMapping("/profile")
    public String viewProfile(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Teacher teacher = teacherService.findByUser(user);
        model.addAttribute("teacher", teacher);
        return "teacher-profile";
    }

    @GetMapping("/profile/edit")
    public String editProfileForm(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Teacher teacher = teacherService.findByUser(user);
        model.addAttribute("teacher", teacher);
        return "edit-teacher-profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(@AuthenticationPrincipal UserDetails userDetails,
                                @ModelAttribute Teacher teacherData,
                                @RequestParam(value = "profileImage", required = false) MultipartFile profileImage,
                                @RequestParam(value = "resume", required = false) MultipartFile resume,
                                Model model) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Teacher existingTeacher = teacherService.findByUser(user);

        // Update basic info
        existingTeacher.setFullName(teacherData.getFullName());
        existingTeacher.setBio(teacherData.getBio());
        existingTeacher.setYearsInClassroom(teacherData.getYearsInClassroom());
        existingTeacher.setSubjectSpecialty(teacherData.getSubjectSpecialty());
        existingTeacher.setDesiredRole(teacherData.getDesiredRole());
        existingTeacher.setPublicProfile(teacherData.isPublicProfile()); // Update publicProfile field

        // Handle profile image upload
        if (profileImage != null && !profileImage.isEmpty()) {
            try {
                String imagePath = fileService.uploadFile(profileImage);
                existingTeacher.setProfileImageUrl(imagePath);
            } catch (IOException e) {
                return "redirect:/teacher/profile/edit?error=upload";
            }
        }

        // Handle resume upload
        if (resume != null && !resume.isEmpty()) {
            try {
                String resumePath = fileService.uploadFile(resume);
                existingTeacher.setResumeUrl(resumePath);
            } catch (IOException e) {
                return "redirect:/teacher/profile/edit?error=upload";
            }
        }

        teacherService.updateProfile(existingTeacher);
        return "redirect:/teacher/profile?updated=true";
    }

    @GetMapping("/saved-jobs")
    public String getSavedJobs(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        Set<Job> savedJobs = teacherService.getSavedJobs(userDetails.getUsername());
        model.addAttribute("savedJobs", savedJobs);
        return "saved-jobs";
    }

    @PostMapping("/save-job")
    public String saveJob(@AuthenticationPrincipal UserDetails userDetails,
                          @RequestParam("jobId") Long jobId,
                          RedirectAttributes redirectAttributes) {
        try {
            teacherService.saveJob(userDetails.getUsername(), jobId);
            redirectAttributes.addFlashAttribute("success", "Job saved successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error saving job: " + e.getMessage());
        }
        return "redirect:/jobs";
    }

    @PostMapping("/unsave-job")
    public String unsaveJob(@AuthenticationPrincipal UserDetails userDetails,
                            @RequestParam("jobId") Long jobId,
                            RedirectAttributes redirectAttributes) {
        try {
            teacherService.unsaveJob(userDetails.getUsername(), jobId);
            redirectAttributes.addFlashAttribute("success", "Job removed from saved jobs.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error removing job: " + e.getMessage());
        }
        return "redirect:/teacher/saved-jobs";
    }
}
