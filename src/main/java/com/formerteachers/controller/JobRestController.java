package com.formerteachers.controller;

import com.formerteachers.model.Job;
import com.formerteachers.dto.JobDTO;
import com.formerteachers.dto.JobPageDTO;
import com.formerteachers.service.JobService;

import org.springframework.data.domain.Page;
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

    @GetMapping
    public JobPageDTO getAllJobs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return toJobPageDTO(jobService.getApprovedJobs(null, null, null, null, page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobDTO> getJobById(@PathVariable Long id) {
        try {
            Job job = jobService.getJobById(id);
            return ResponseEntity.ok(toDTO(job));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/search")
    public JobPageDTO searchJobs(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String workType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return toJobPageDTO(jobService.getApprovedJobs(keyword, location, category, workType, page, size));
    }

    @PostMapping
    public ResponseEntity<JobDTO> createJob(@Valid @RequestBody Job job) {
        Job savedJob = jobService.saveJob(job);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedJob.getId())
                .toUri();
        return ResponseEntity.created(location).body(toDTO(savedJob));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteJob(@PathVariable Long id) {
        jobService.deleteJobById(id);
        return ResponseEntity.noContent().build();
    }
}
