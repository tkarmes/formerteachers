package com.formerteachers.controller;

import com.formerteachers.model.Job;
import com.formerteachers.model.Teacher;
import com.formerteachers.service.TeacherService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Set;

@Controller
public class TeacherController {

    private final TeacherService teacherService;

    public TeacherController(TeacherService teacherService) {
        this.teacherService = teacherService;
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

    @PostMapping("/teacher/profile/edit")
    public String updateProfile(@ModelAttribute Teacher teacher, @AuthenticationPrincipal UserDetails userDetails) {
        teacherService.updateProfile(userDetails.getUsername(), teacher);
        return "redirect:/teacher/profile";
    }
}
