package com.formerteachers.controller;

import com.formerteachers.model.Job;
import com.formerteachers.repository.JobRepository;
import com.formerteachers.dto.JobDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/jobs")
public class JobRestController {

    private final JobRepository jobRepository;

    public JobRestController(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    // Convert Job → JobDTO
    private JobDTO toDTO(Job job) {
        return new JobDTO(
                job.getId(),
                job.getTitle(),
                job.getCompany(),
                job.getLocation(),
                job.getSalaryRange(),
                job.getDescription()
        );
    }

    // GET all jobs
    @GetMapping
    public List<JobDTO> getAllJobs() {
        return jobRepository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }

    // GET single job
    @GetMapping("/{id}")
    public ResponseEntity<JobDTO> getJobById(@PathVariable Long id) {
        return jobRepository.findById(id)
                .map(job -> ResponseEntity.ok(toDTO(job)))
                .orElse(ResponseEntity.notFound().build());
    }

    // CREATE job
    @PostMapping
    public ResponseEntity<JobDTO> createJob(@Valid @RequestBody Job job) {
        Job savedJob = jobRepository.save(job);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedJob.getId())
                .toUri();
        return ResponseEntity.created(location).body(toDTO(savedJob));
    }

    // UPDATE job
    @PutMapping("/{id}")
    public ResponseEntity<JobDTO> updateJob(@PathVariable Long id,
                                            @Valid @RequestBody Job updatedJob) {
        return jobRepository.findById(id)
                .map(job -> {
                    job.setTitle(updatedJob.getTitle());
                    job.setCompany(updatedJob.getCompany());
                    job.setLocation(updatedJob.getLocation());
                    job.setSalaryRange(updatedJob.getSalaryRange());
                    job.setDescription(updatedJob.getDescription());
                    jobRepository.save(job);
                    return ResponseEntity.ok(toDTO(job));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE job
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteJob(@PathVariable Long id) {
        return jobRepository.findById(id)
                .map(job -> {
                    jobRepository.delete(job);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}