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
    public String createJob(@ModelAttribute Job job, RedirectAttributes redirect) {
        jobService.save(job);
        redirect.addFlashAttribute("successMessage", "Job posted successfully!");
        return "redirect:/jobs";
    }


// ====================== ADMIN SECTION (Managed by Spring Security) ======================

    // This method now handles the page you see *after* a successful login.
// The URL matches the defaultSuccessUrl in your SecurityConfig.
    @GetMapping("/admin")
    public String adminJobs(@RequestParam(defaultValue = "0") int page,
                            @RequestParam(defaultValue = "20") int size,
                            Model model) {

        // No more password check! Spring Security has already done it.
        // If the user's code reaches this point, they are authenticated and have the ADMIN role.

        Pageable pageable = PageRequest.of(page, size);
        Page<Job> jobsPage = jobService.getAllJobs(pageable);

        // The model attributes for your admin-jobs.html page
        model.addAttribute("jobs", jobsPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", jobsPage.getTotalPages());

        return "admin-jobs"; // Renders the admin-jobs.html template
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
        return "redirect:/jobs/admin"; // Redirect back to the main admin page
    }

    // Admin: Delete job
    @PostMapping("/admin/jobs/{id}/delete")
    public String adminDeleteJob(@PathVariable Long id) {
        jobService.deleteById(id);
        return "redirect:/jobs/admin"; // Redirect back to the main admin page
    }


    @GetMapping("/for-employers")
    public String forEmployers() {
        return "for-employers";    }

}