package com.formerteachers.controller;

import com.formerteachers.model.Job;
import com.formerteachers.dto.JobDTO;
import com.formerteachers.dto.JobPageDTO;
import com.formerteachers.service.JobService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/jobs")
public class JobRestController {

    private final JobService jobService;

    public JobRestController(JobService jobService) {
        this.jobService = jobService;
    }

    // Convert Job → DTO
    private JobDTO toDTO(Job job) {
        return new JobDTO(
                job.getId(),
                job.getTitle(),
                job.getCompany(),
                job.getLocation(),
                job.getSalaryRange(),
                job.getDescription(),
                job.getCategory()
        );
    }

    // Convert Page<Job> → JobPageDTO
    private JobPageDTO toJobPageDTO(Page<Job> page) {
        List<JobDTO> content = page.getContent().stream()
                .map(this::toDTO)
                .toList();

        return new JobPageDTO(
                content,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast()
        );
    }

    // GET all jobs (paginated, newest first by default)
    @GetMapping
    public JobPageDTO getAllJobs(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return toJobPageDTO(jobService.getAllJobs(pageable));
    }

    // GET job by ID
    @GetMapping("/{id}")
    public ResponseEntity<JobDTO> getJobById(@PathVariable Long id) {
        return jobService.getJobById(id)
                .map(job -> ResponseEntity.ok(toDTO(job)))
                .orElse(ResponseEntity.notFound().build());
    }

    // FILTER by company
    @GetMapping("/company/{company}")
    public JobPageDTO getJobsByCompany(
            @PathVariable String company,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return toJobPageDTO(jobService.getJobsByCompany(company, pageable));
    }

    // FILTER by category
    @GetMapping("/category/{category}")
    public JobPageDTO getJobsByCategory(
            @PathVariable String category,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return toJobPageDTO(jobService.getJobsByCategory(category, pageable));
    }

    // FILTER by location
    @GetMapping("/location/{location}")
    public JobPageDTO getJobsByLocation(
            @PathVariable String location,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return toJobPageDTO(jobService.getJobsByLocation(location, pageable));
    }

    // SEARCH across title, description, or category
    @GetMapping("/search")
    public JobPageDTO searchJobs(
            @RequestParam String keyword,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return toJobPageDTO(jobService.searchJobs(keyword, pageable));
    }

    // CREATE job
    @PostMapping
    public ResponseEntity<JobDTO> createJob(@Valid @RequestBody Job job) {
        Job savedJob = jobService.save(job);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedJob.getId())
                .toUri();

        return ResponseEntity.created(location).body(toDTO(savedJob));
    }

    // UPDATE job
    @PutMapping("/{id}")
    public ResponseEntity<JobDTO> updateJob(@PathVariable Long id, @Valid @RequestBody Job updatedJob) {
        return jobService.getJobById(id)
                .map(job -> {
                    job.setTitle(updatedJob.getTitle());
                    job.setCompany(updatedJob.getCompany());
                    job.setLocation(updatedJob.getLocation());
                    job.setSalaryRange(updatedJob.getSalaryRange());
                    job.setDescription(updatedJob.getDescription());
                    job.setCategory(updatedJob.getCategory());

                    Job savedJob = jobService.save(job);
                    return ResponseEntity.ok(toDTO(savedJob));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE job
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteJob(@PathVariable Long id) {
        return jobService.getJobById(id)
                .map(job -> {
                    jobService.delete(job);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}