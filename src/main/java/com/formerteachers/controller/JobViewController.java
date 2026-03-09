package com.formerteachers.controller;

import com.formerteachers.model.Job;
import com.formerteachers.service.JobService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/jobs")
public class JobViewController {

    private final JobService jobService;

    public JobViewController(JobService jobService) {
        this.jobService = jobService;
    }

    // Show all jobs
    @GetMapping
    public String listJobs(Model model) {
        model.addAttribute("jobs", jobService.getAllJobs());
        return "jobs";
    }

    // Show create form
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("job", new Job());
        return "create-job";
    }

    // Save new job
    @PostMapping
    public String createJob(@ModelAttribute Job job) {
        jobService.save(job);
        return "redirect:/jobs";
    }

    // Show edit form
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        Job job = jobService.getJobById(id).orElseThrow();
        model.addAttribute("job", job);
        return "edit-job";
    }

    @GetMapping("/{id}")
    public String viewJob(@PathVariable Long id, Model model) {

        Job job = jobService.getJobById(id).orElseThrow();

        model.addAttribute("job", job);

        return "job-detail";
    }

    // Update job
    @PostMapping("/update/{id}")
    public String updateJob(@PathVariable Long id, @ModelAttribute Job updatedJob) {

        Job job = jobService.getJobById(id).orElseThrow();

        job.setTitle(updatedJob.getTitle());
        job.setCompany(updatedJob.getCompany());
        job.setLocation(updatedJob.getLocation());
        job.setSalaryRange(updatedJob.getSalaryRange());
        job.setDescription(updatedJob.getDescription());
        job.setCategory(updatedJob.getCategory());
        job.setWorkType(updatedJob.getWorkType());

        jobService.save(job);

        return "redirect:/jobs";
    }

    // Delete job
    @PostMapping("/{id}")
    public String deleteJob(@PathVariable Long id) {
        jobService.deleteById(id);
        return "redirect:/jobs";
    }
}