package com.formerteachers.controller;

import com.formerteachers.service.JobService;
import com.formerteachers.service.JobAggregatorService;
import com.formerteachers.model.Job;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {

    private final JobService jobService;
    private final JobAggregatorService jobAggregatorService;

    public HomeController(JobService jobService, JobAggregatorService jobAggregatorService) {
        this.jobService = jobService;
        this.jobAggregatorService = jobAggregatorService;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("title", "FormerTeachers.com");
        model.addAttribute("message", "Welcome! This site is dedicated to helping former teachers find meaningful roles.");
        model.addAttribute("subtext", "Jobs in edtech, instructional design, curriculum, and more.");

        // ⭐ Show newest 3 jobs
        List<Job> jobs = jobService.getFeaturedJobs();
        model.addAttribute("jobs", jobs);

        return "index";
    }

    // Optional: trigger import when visiting /import-home
    @GetMapping("/import-home")
    public String importJobsFromApi(Model model) {
        String apiUrl = "https://some-public-api/jobs?keywords=edtech";

        // Pass 2 arguments: API URL + keyword/category
        int importedCount = jobAggregatorService.importJobsFromApi(apiUrl, "edtech");

        model.addAttribute("importMessage", "Imported " + importedCount + " new jobs!");

        // Show newest 3 jobs after import
        List<Job> jobs = jobService.getFeaturedJobs();
        model.addAttribute("jobs", jobs);

        return "index";
    }
}