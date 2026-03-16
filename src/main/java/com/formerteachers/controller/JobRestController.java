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

import jakarta.annotation.PostConstruct;
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


    // ===============================
    // HELPER: Job → DTO
    // ===============================
    private JobDTO toDTO(Job job) {
        return new JobDTO(
                job.getId(),
                job.getTitle(),
                job.getCompany(),
                job.getLocation(),
                job.getSalaryRange(),
                job.getDescription(),
                job.getCategory(),
                job.getWorkType(),
                job.getCreatedAt()
        );
    }

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

    // ===============================
    // API ENDPOINTS
    // ===============================

    @GetMapping
    public JobPageDTO getAllJobs(@PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return toJobPageDTO(jobService.getAllJobs(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobDTO> getJobById(@PathVariable Long id) {
        return jobService.getJobById(id)
                .map(job -> ResponseEntity.ok(toDTO(job)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public JobPageDTO searchJobs(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String workType,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        return toJobPageDTO(jobService.searchJobs(keyword, location, category, workType, pageable));
    }

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
                    job.setWorkType(updatedJob.getWorkType());
                    Job savedJob = jobService.save(job);
                    return ResponseEntity.ok(toDTO(savedJob));
                })
                .orElse(ResponseEntity.notFound().build());
    }

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