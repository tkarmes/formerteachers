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

        // 1. Get the top 3 approved jobs for the homepage
        List<Job> featuredJobs = jobService.getFeaturedJobs();

        // 2. Add data to the view
        model.addAttribute("featuredJobs", featuredJobs);
        model.addAttribute("hasJobs", !featuredJobs.isEmpty());

        // 3. Return "index" to load index.html
        return "index";
    }
}