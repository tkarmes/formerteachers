package com.formerteachers.controller;

import com.formerteachers.service.JobService;
import com.formerteachers.model.Job;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {

    private final JobService jobService;

    public HomeController(JobService jobService) {
        this.jobService = jobService;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("title", "FormerTeachers.com");
        model.addAttribute("message", "Welcome! Helping former teachers transition to rewarding careers outside the classroom.");
        model.addAttribute("subtext", "Discover opportunities in instructional design, learning & development, corporate training, edtech, customer success, and more.");

        // Show the 3 newest/featured jobs (or empty list if none yet)
        List<Job> featuredJobs = jobService.getFeaturedJobs();
        model.addAttribute("featuredJobs", featuredJobs);  // renamed for clarity

        // Optional: Add a flag to show/hide a "No jobs yet" message in Thymeleaf
        model.addAttribute("hasJobs", !featuredJobs.isEmpty());

        return "index";
    }

    // If you want a dedicated "Post a Job" page later, add this stub (uncomment when ready)
    // @GetMapping("/post-job")
    // public String postJobForm(Model model) {
    //     model.addAttribute("job", new Job());  // for form binding
    //     return "post-job";  // create this template next
    // }
}