package com.formerteachers.controller;

import com.formerteachers.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final JobService jobService;

    @Autowired
    public AdminController(JobService jobService) {
        this.jobService = jobService;
    }

    @GetMapping("/dashboard")
    public String adminDashboard(Model model) {
        model.addAttribute("pendingJobs", jobService.getPendingJobs());
        return "admin-dashboard";
    }

    @PostMapping("/approve/{id}")
    public String approveJob(@PathVariable Long id) {
        jobService.approveJob(id);
        return "redirect:/admin/dashboard?approved";
    }

    @PostMapping("/delete/{id}")
    public String deleteJob(@PathVariable Long id) {
        jobService.deleteJobById(id);
        return "redirect:/admin/dashboard?deleted";
    }
}
