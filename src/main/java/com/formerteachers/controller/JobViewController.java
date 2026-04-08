package com.formerteachers.controller;

import com.formerteachers.model.EmployerProfile;
import com.formerteachers.model.Job;
import com.formerteachers.model.User;
import com.formerteachers.repository.EmployerProfileRepository;
import com.formerteachers.repository.UserRepository;
import com.formerteachers.service.JobService;
import com.formerteachers.service.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/jobs")
public class JobViewController {

    private final JobService jobService;
    private final EmployerProfileRepository employerProfileRepository;
    private final UserRepository userRepository;
    private final TeacherService teacherService;

    @Autowired
    public JobViewController(JobService jobService, 
                             EmployerProfileRepository employerProfileRepository, 
                             UserRepository userRepository,
                             TeacherService teacherService) {
        this.jobService = jobService;
        this.employerProfileRepository = employerProfileRepository;
        this.userRepository = userRepository;
        this.teacherService = teacherService;
    }

    @GetMapping
    public String listJobs(Model model,
                           @RequestParam(required = false) String keyword,
                           @RequestParam(required = false) String location,
                           @RequestParam(required = false) String category,
                           @RequestParam(required = false) String workType,
                           @RequestParam(defaultValue = "0") int page,
                           @RequestParam(defaultValue = "10") int size) {
        
        Page<Job> jobPage = jobService.getApprovedJobs(keyword, location, category, workType, page, size);
        
        // Add saved jobs info for teachers
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getPrincipal().equals("anonymousUser")) {
            boolean isTeacher = auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_TEACHER"));
            if (isTeacher) {
                Set<Long> savedJobIds = teacherService.getSavedJobs(auth.getName())
                        .stream().map(Job::getId).collect(Collectors.toSet());
                model.addAttribute("savedJobIds", savedJobIds);
            }
        }
        
        model.addAttribute("jobs", jobPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", jobPage.getTotalPages());
        model.addAttribute("keyword", keyword);
        model.addAttribute("location", location);
        model.addAttribute("selectedCategory", category);
        model.addAttribute("selectedWorkType", workType);
        
        return "jobs";
    }

    @GetMapping("/{id}")
    public String viewJob(@PathVariable Long id, Model model) {
        Job job = jobService.getJobById(id);
        model.addAttribute("job", job);
        
        // Add saved jobs info for teachers
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getPrincipal().equals("anonymousUser")) {
            boolean isTeacher = auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_TEACHER"));
            if (isTeacher) {
                Set<Long> savedJobIds = teacherService.getSavedJobs(auth.getName())
                        .stream().map(Job::getId).collect(Collectors.toSet());
                model.addAttribute("isSaved", savedJobIds.contains(id));
            }
        }
        
        return "job-detail";
    }

    @GetMapping("/create")
    public String showCreateJobForm(Model model) {
        model.addAttribute("job", new Job());
        model.addAttribute("isEdit", false);
        return "create-job";
    }

    @PostMapping("/create")
    public String createJob(@ModelAttribute Job job) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
        
        EmployerProfile employer = employerProfileRepository.findByUser(user)
                .orElseGet(() -> {
                    EmployerProfile newProfile = new EmployerProfile();
                    newProfile.setUser(user);
                    newProfile.setCompanyName(job.getCompany());
                    return employerProfileRepository.save(newProfile);
                });
        
        job.setEmployer(employer);
        job.setApproved(false); // New jobs require approval
        
        jobService.saveJob(job);
        return "redirect:/jobs/dashboard?created=true";
    }

    @GetMapping("/dashboard")
    public String employerDashboard(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
        
        EmployerProfile employer = employerProfileRepository.findByUser(user)
                .orElseGet(() -> {
                    EmployerProfile newProfile = new EmployerProfile();
                    newProfile.setUser(user);
                    newProfile.setCompanyName(user.getUsername());
                    return employerProfileRepository.save(newProfile);
                });
        
        List<Job> employerJobs = jobService.getJobsByEmployer(employer);
        
        model.addAttribute("jobs", employerJobs);
        model.addAttribute("profile", employer);
        return "employer-dashboard";
    }

    @GetMapping("/edit/{id}")
    public String showEditJobForm(@PathVariable Long id, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User user = userRepository.findByUsername(username).orElseThrow();
        EmployerProfile employer = employerProfileRepository.findByUser(user).orElseThrow();
        
        Job job = jobService.getJobByIdAndEmployer(id, employer);
        model.addAttribute("job", job);
        model.addAttribute("isEdit", true);
        return "create-job";
    }

    @PostMapping("/edit/{id}")
    public String updateJob(@PathVariable Long id, @ModelAttribute Job updatedJob) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User user = userRepository.findByUsername(username).orElseThrow();
        EmployerProfile employer = employerProfileRepository.findByUser(user).orElseThrow();
        
        Job existingJob = jobService.getJobByIdAndEmployer(id, employer);
        
        // Update fields
        existingJob.setTitle(updatedJob.getTitle());
        existingJob.setDescription(updatedJob.getDescription());
        existingJob.setCategory(updatedJob.getCategory());
        existingJob.setLocation(updatedJob.getLocation());
        existingJob.setWorkType(updatedJob.getWorkType());
        existingJob.setSalaryRange(updatedJob.getSalaryRange());
        existingJob.setApplyInfo(updatedJob.getApplyInfo());
        existingJob.setApproved(false); // Changes require re-approval
        
        jobService.saveJob(existingJob);
        return "redirect:/jobs/dashboard?updated=true";
    }

    @GetMapping("/delete/{id}")
    public String deleteJob(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User user = userRepository.findByUsername(username).orElseThrow();
        EmployerProfile employer = employerProfileRepository.findByUser(user).orElseThrow();
        
        // Verify ownership before deleting
        jobService.getJobByIdAndEmployer(id, employer);
        jobService.deleteJobById(id);
        
        return "redirect:/jobs/dashboard?deleted=true";
    }
}
