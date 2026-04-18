package com.formerteachers.controller;

import com.formerteachers.model.*;
import com.formerteachers.repository.JobRepository;
import com.formerteachers.repository.UserRepository;
import com.formerteachers.service.ApplicationService;
import com.formerteachers.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/jobs")
public class JobViewController {

    @Autowired
    private JobService jobService;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ApplicationService applicationService;

    @GetMapping
    public String listJobs(Model model) {
        List<Job> jobs = jobService.getAllJobs();
        model.addAttribute("jobs", jobs);
        
        // Initialize with default values
        model.addAttribute("appliedJobIds", new HashSet<Long>());
        model.addAttribute("hasResume", false);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            User user = userRepository.findByUsername(auth.getName()).orElse(null);
            if (user != null && "TEACHER".equals(user.getRole())) {
                Teacher teacher = user.getTeacherProfile();
                if (teacher != null) {
                    Set<Long> appliedJobIds = applicationService.getTeacherApplications(teacher)
                            .stream()
                            .map(app -> app.getJob().getId())
                            .collect(Collectors.toSet());
                    model.addAttribute("appliedJobIds", appliedJobIds);
                    model.addAttribute("hasResume", teacher.getResumeUrl() != null && !teacher.getResumeUrl().isEmpty());
                }
            }
        }
        
        return "jobs";
    }

    @GetMapping("/{id}")
    public String viewJob(@PathVariable Long id, Model model) {
        Job job = jobService.getJobById(id);
        model.addAttribute("job", job);

        // Initialize with default values to prevent Thymeleaf/SpEL null pointer exceptions
        model.addAttribute("hasApplied", false);
        model.addAttribute("hasResume", false);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            User user = userRepository.findByUsername(auth.getName()).orElse(null);
            if (user != null) {
                if ("TEACHER".equals(user.getRole())) {
                    Teacher teacher = user.getTeacherProfile();
                    model.addAttribute("teacher", teacher);
                    if (teacher != null) {
                        model.addAttribute("hasApplied", applicationService.hasApplied(teacher, job));
                        model.addAttribute("hasResume", teacher.getResumeUrl() != null && !teacher.getResumeUrl().isEmpty());
                    }
                }
            }
        }

        return "job-detail";
    }

    @PostMapping("/{id}/apply")
    public String applyForJob(@PathVariable Long id, @RequestParam(required = false) String coverLetter, 
                             HttpServletRequest request, RedirectAttributes redirectAttributes) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getName().equals("anonymousUser")) {
            return "redirect:/login";
        }

        User user = userRepository.findByUsername(auth.getName()).orElse(null);
        if (user == null || !user.getRole().equals("TEACHER")) {
            redirectAttributes.addFlashAttribute("error", "Only teachers can apply for jobs.");
            return "redirect:/jobs/" + id;
        }

        Teacher teacher = user.getTeacherProfile();
        if (teacher == null || teacher.getResumeUrl() == null || teacher.getResumeUrl().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Please upload your resume before applying.");
            return "redirect:/teacher/profile/edit";
        }

        Job job = jobService.getJobById(id);
        applicationService.apply(teacher, job, coverLetter);

        redirectAttributes.addFlashAttribute("success", "Application submitted successfully!");
        
        // Redirect back to where the user came from (either search results or job detail)
        String referer = request.getHeader("Referer");
        if (referer != null && !referer.isEmpty()) {
            return "redirect:" + referer;
        }
        return "redirect:/jobs/" + id;
    }

    @GetMapping("/create")
    public String showPostJobForm(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getName().equals("anonymousUser")) {
            return "redirect:/login";
        }
        model.addAttribute("job", new Job());
        model.addAttribute("isEdit", false);
        return "create-job";
    }

    @PostMapping("/create")
    public String postJob(@ModelAttribute Job job) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUsername(auth.getName()).orElse(null);
        if (user != null && user.getEmployerProfile() != null) {
            job.setEmployer(user.getEmployerProfile());
            jobService.saveJob(job);
        }
        return "redirect:/jobs/dashboard";
    }

    @GetMapping("/dashboard")
    public String employerDashboard(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getName().equals("anonymousUser")) {
            return "redirect:/login";
        }
        User user = userRepository.findByUsername(auth.getName()).orElse(null);
        if (user == null || !user.getRole().equals("EMPLOYER")) {
            return "redirect:/";
        }
        
        EmployerProfile employer = user.getEmployerProfile();
        if (employer != null) {
            List<Job> jobs = jobService.getJobsByEmployer(employer);
            model.addAttribute("jobs", jobs);
            model.addAttribute("profile", employer);
        }
        
        return "employer-dashboard";
    }

    @GetMapping("/edit/{id}")
    public String showEditJobForm(@PathVariable Long id, Model model) {
        Job job = jobService.getJobById(id);
        model.addAttribute("job", job);
        model.addAttribute("isEdit", true);
        return "create-job";
    }

    @PostMapping("/edit/{id}")
    public String updateJob(@PathVariable Long id, @ModelAttribute Job job) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUsername(auth.getName()).orElse(null);
        if (user != null && user.getEmployerProfile() != null) {
            Job existingJob = jobService.getJobById(id);
            if (existingJob != null && existingJob.getEmployer().getId().equals(user.getEmployerProfile().getId())) {
                job.setEmployer(user.getEmployerProfile());
                job.setId(id);
                // Reset approval on edit if required by business logic, 
                // but for now let's just save.
                jobService.saveJob(job);
            }
        }
        return "redirect:/jobs/dashboard";
    }

    @PostMapping("/delete/{id}")
    public String deleteJob(@PathVariable Long id) {
        jobService.deleteJob(id);
        return "redirect:/jobs/dashboard";
    }

    @GetMapping("/search")
    public String searchJobs(@RequestParam String query, Model model) {
        List<Job> jobs = jobService.searchJobs(query);
        model.addAttribute("jobs", jobs);
        model.addAttribute("query", query);

        // Initialize with default values
        model.addAttribute("appliedJobIds", new HashSet<Long>());
        model.addAttribute("hasResume", false);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            User user = userRepository.findByUsername(auth.getName()).orElse(null);
            if (user != null && "TEACHER".equals(user.getRole())) {
                Teacher teacher = user.getTeacherProfile();
                if (teacher != null) {
                    Set<Long> appliedJobIds = applicationService.getTeacherApplications(teacher)
                            .stream()
                            .map(app -> app.getJob().getId())
                            .collect(Collectors.toSet());
                    model.addAttribute("appliedJobIds", appliedJobIds);
                    model.addAttribute("hasResume", teacher.getResumeUrl() != null && !teacher.getResumeUrl().isEmpty());
                }
            }
        }

        return "jobs";
    }

    @GetMapping("/applicants/{jobId}")
    public String viewApplicants(@PathVariable Long jobId, Model model, RedirectAttributes redirectAttributes) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUsername(auth.getName()).orElse(null);

        Job job = jobService.getJobById(jobId);
        if (user == null || user.getEmployerProfile() == null || job == null || 
            !job.getEmployer().getId().equals(user.getEmployerProfile().getId())) {
            redirectAttributes.addFlashAttribute("error", "You do not have permission to view these applicants.");
            return "redirect:/jobs/dashboard";
        }

        List<Application> applications = applicationService.getJobApplications(job);
        model.addAttribute("job", job);
        model.addAttribute("applications", applications);
        return "job-applicants.html";
    }

    @PostMapping("/applications/{id}/status")
    public String updateApplicationStatus(@PathVariable Long id, @RequestParam String status, RedirectAttributes redirectAttributes) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUsername(auth.getName()).orElse(null);

        Optional<Application> optionalApplication = applicationService.getById(id);
        if (optionalApplication.isPresent() && user != null && user.getEmployerProfile() != null) {
            Application application = optionalApplication.get();
            // Verify ownership
            if (application.getJob().getEmployer().getId().equals(user.getEmployerProfile().getId())) {
                applicationService.updateStatus(id, status);
                redirectAttributes.addFlashAttribute("success", "Application status updated to " + status);
                return "redirect:/jobs/applicants/" + application.getJob().getId();
            }
        }

        redirectAttributes.addFlashAttribute("error", "Failed to update application status.");
        return "redirect:/jobs/dashboard";
    }

    @PostMapping("/applications/{id}/delete")
    public String deleteApplication(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUsername(auth.getName()).orElse(null);

        Optional<Application> optionalApplication = applicationService.getById(id);
        if (optionalApplication.isPresent() && user != null && user.getEmployerProfile() != null) {
            Application application = optionalApplication.get();
            // Verify ownership
            if (application.getJob().getEmployer().getId().equals(user.getEmployerProfile().getId())) {
                Long jobId = application.getJob().getId();
                applicationService.delete(id);
                redirectAttributes.addFlashAttribute("success", "Application deleted successfully.");
                return "redirect:/jobs/applicants/" + jobId;
            }
        }

        redirectAttributes.addFlashAttribute("error", "Failed to delete application.");
        return "redirect:/jobs/dashboard";
    }
}
