package com.formerteachers.controller;

import com.formerteachers.model.Job;
import com.formerteachers.service.JobService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    // Show all jobs with optional search/filter
    @GetMapping
    public String listJobs(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String workType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model
    ) {
        Pageable pageable = PageRequest.of(page, size);

        Page<Job> jobs = jobService.searchJobs(keyword, location, category, workType, pageable);

        // ✅ Pass the Page object, not just content
        model.addAttribute("jobs", jobs);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", jobs.getTotalPages());
        model.addAttribute("keyword", keyword);
        model.addAttribute("location", location);
        model.addAttribute("category", category);
        model.addAttribute("workType", workType);

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
//    @GetMapping("/{id}/edit")
//    public String showEditForm(@PathVariable Long id, Model model) {
//        Job job = jobService.getJobById(id).orElseThrow();
//        model.addAttribute("job", job);
//        return "edit-job";
//    }

    @GetMapping("/{id}")
    public String viewJob(@PathVariable Long id, Model model) {
        Job job = jobService.getJobById(id).orElseThrow();
        model.addAttribute("job", job);
        return "job-detail";
    }

    // Update job
//    @PostMapping("/update/{id}")
//    public String updateJob(@PathVariable Long id, @ModelAttribute Job updatedJob) {
//        Job job = jobService.getJobById(id).orElseThrow();
//
//        job.setTitle(updatedJob.getTitle());
//        job.setCompany(updatedJob.getCompany());
//        job.setLocation(updatedJob.getLocation());
//        job.setSalaryRange(updatedJob.getSalaryRange());
//        job.setDescription(updatedJob.getDescription());
//        job.setCategory(updatedJob.getCategory());
//        job.setWorkType(updatedJob.getWorkType());
//
//        jobService.save(job);
//
//        return "redirect:/jobs";
//    }

    // Delete job
//    @PostMapping("/{id}/delete")
//    public String deleteJob(@PathVariable Long id) {
//        jobService.deleteById(id);
//        return "redirect:/jobs";
//    }
}