package com.formerteachers.controller;

import com.formerteachers.model.Job;
import com.formerteachers.repository.JobRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/jobs")
public class JobController {

    private final JobRepository jobRepository;

    public JobController(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    // Display all jobs
    @GetMapping
    public String listJobs(Model model) {
        model.addAttribute("jobs", jobRepository.findAll());
        return "jobs";
    }

    // Show form to create a new job
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("job", new Job());
        return "create-job";
    }

    // Save a new job
    @PostMapping
    public String createJob(@ModelAttribute Job job) {
        jobRepository.save(job);
        return "redirect:/jobs";
    }

    // Show form to edit an existing job
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid job Id: " + id));
        model.addAttribute("job", job);
        return "edit-job";
    }

    // Update an existing job
    @PutMapping("/{id}")
    public String updateJob(@PathVariable Long id, @ModelAttribute Job updatedJob) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid job Id: " + id));

        job.setTitle(updatedJob.getTitle());
        job.setCompany(updatedJob.getCompany());
        job.setLocation(updatedJob.getLocation());
        job.setSalaryRange(updatedJob.getSalaryRange());

        jobRepository.save(job);
        return "redirect:/jobs";
    }

    // Delete a job
    @DeleteMapping("/{id}")
    public String deleteJob(@PathVariable Long id) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid job Id: " + id));
        jobRepository.delete(job);
        return "redirect:/jobs";
    }
}