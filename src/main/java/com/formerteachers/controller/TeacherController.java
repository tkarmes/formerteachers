package com.formerteachers.controller;

import com.formerteachers.model.Job;
import com.formerteachers.model.Teacher;
import com.formerteachers.service.TeacherService;
import com.formerteachers.service.FileService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Set;

@Controller
public class TeacherController {

    private final TeacherService teacherService;
    private final FileService fileService;

    public TeacherController(TeacherService teacherService, FileService fileService) {
        this.teacherService = teacherService;
        this.fileService = fileService;
    }

    @PostMapping("/teacher/save-job")
    public String saveJob(@RequestParam Long jobId, @AuthenticationPrincipal UserDetails userDetails) {
        teacherService.saveJob(userDetails.getUsername(), jobId);
        return "redirect:/jobs";
    }

    @PostMapping("/teacher/unsave-job")
    public String unsaveJob(@RequestParam Long jobId, @AuthenticationPrincipal UserDetails userDetails) {
        teacherService.unsaveJob(userDetails.getUsername(), jobId);
        return "redirect:/teacher/saved-jobs";
    }

    @GetMapping("/teacher/saved-jobs")
    public String getSavedJobs(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        Set<Job> savedJobs = teacherService.getSavedJobs(userDetails.getUsername());
        model.addAttribute("savedJobs", savedJobs);
        return "saved-jobs";
    }

    @GetMapping("/teacher/profile")
    public String viewProfile(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        Teacher teacher = teacherService.getTeacherProfile(userDetails.getUsername());
        model.addAttribute("teacher", teacher);
        return "teacher-profile";
    }

    @GetMapping("/teacher/profile/edit")
    public String editProfileForm(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        Teacher teacher = teacherService.getTeacherProfile(userDetails.getUsername());
        model.addAttribute("teacher", teacher);
        return "edit-teacher-profile";
    }

    @PostMapping("/teacher/profile/update")
    public String updateProfile(@ModelAttribute Teacher teacher, 
                                @RequestParam(value = "profileImage", required = false) MultipartFile profileImage,
                                @AuthenticationPrincipal UserDetails userDetails) throws IOException {
        
        if (profileImage != null && !profileImage.isEmpty()) {
            String imageUrl = fileService.uploadFile(profileImage);
            teacher.setProfileImageUrl(imageUrl);
        }
        
        teacherService.updateProfile(userDetails.getUsername(), teacher);
        return "redirect:/teacher/profile";
    }
}
