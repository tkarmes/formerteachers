package com.formerteachers.controller;

import com.formerteachers.model.Job;
import com.formerteachers.service.JobService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;

@Controller
@RequestMapping("/jobs")
public class JobViewController {

    private final JobService jobService;

    public JobViewController(JobService jobService) {
        this.jobService = jobService;
    }

    // Public: Browse jobs
    @GetMapping
    public String listJobs(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String workType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Job> jobs = jobService.searchJobs(keyword, location, category, workType, pageable);

        model.addAttribute("jobs", jobs);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", jobs.getTotalPages());
        model.addAttribute("keyword", keyword);
        model.addAttribute("location", location);
        model.addAttribute("category", category);
        model.addAttribute("workType", workType);

        return "jobs";
    }

    // Public: View single job
    @GetMapping("/{id}")
    public String viewJob(@PathVariable Long id, Model model) {
        Job job = jobService.getJobById(id).orElseThrow();
        model.addAttribute("job", job);
        return "job-detail";
    }

    // Public: Show create form
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("job", new Job());
        return "create-job";
    }

    // Public: Save new job
    @PostMapping
    public String createJob(@Valid @ModelAttribute Job job, BindingResult result, RedirectAttributes redirect, Model model) {
        if (result.hasErrors()) {
            return "create-job";
        }

        // Explicitly set approved to false (just to be safe)
        job.setApproved(false);
        jobService.save(job);

        // Success message
        redirect.addFlashAttribute("successMessage", "Job submitted successfully! It will be visible after admin approval.");
        return "redirect:/jobs";
    }

    // ====================== ADMIN SECTION ======================

    @GetMapping("/admin")
    public String adminJobs(@RequestParam(defaultValue = "0") int page,
                            @RequestParam(defaultValue = "20") int size,
                            Model model) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Job> jobsPage = jobService.getAllJobs(pageable);

        model.addAttribute("jobs", jobsPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", jobsPage.getTotalPages());

        return "admin-jobs";
    }

    // Admin: Show edit form
    @GetMapping("/admin/jobs/{id}/edit")
    public String adminShowEditForm(@PathVariable Long id, Model model) {
        Job job = jobService.getJobById(id).orElseThrow();
        model.addAttribute("job", job);
        return "edit-job";
    }

    // Admin: Save edited job
    @PostMapping("/admin/jobs/update/{id}")
    public String adminUpdateJob(@PathVariable Long id, @ModelAttribute Job updatedJob) {
        Job job = jobService.getJobById(id).orElseThrow();
        job.setTitle(updatedJob.getTitle());
        job.setCompany(updatedJob.getCompany());
        job.setLocation(updatedJob.getLocation());
        job.setSalaryRange(updatedJob.getSalaryRange());
        job.setDescription(updatedJob.getDescription());
        job.setCategory(updatedJob.getCategory());
        job.setWorkType(updatedJob.getWorkType());
        job.setApplyInfo(updatedJob.getApplyInfo());

        jobService.save(job);
        return "redirect:/jobs/admin";
    }

    // Admin: Delete job
    @PostMapping("/admin/jobs/{id}/delete")
    public String adminDeleteJob(@PathVariable Long id) {
        jobService.deleteById(id);
        return "redirect:/jobs/admin";
    }

    // Admin: Approve job
    @PostMapping("/admin/jobs/{id}/approve")
    public String adminApproveJob(@PathVariable Long id) {
        Job job = jobService.getJobById(id).orElseThrow();
        job.setApproved(true);
        jobService.save(job);
        return "redirect:/jobs/admin";
    }

    @GetMapping("/for-employers")
    public String forEmployers() {
        return "for-employers";
    }
}